package com.emdp.rickandmorty.data.source.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.emdp.rickandmorty.core.common.result.AppError
import com.emdp.rickandmorty.core.common.result.DataResult
import com.emdp.rickandmorty.data.source.local.RickAndMortyDatabase
import com.emdp.rickandmorty.data.source.local.entity.CharacterEntity
import com.emdp.rickandmorty.data.source.local.entity.CharacterRemoteKeysEntity
import com.emdp.rickandmorty.data.source.local.mapper.CharacterLocalMapper
import com.emdp.rickandmorty.data.source.remote.CharactersRemoteSource
import com.emdp.rickandmorty.domain.models.CharactersFilterModel
import com.emdp.rickandmorty.domain.models.CharactersPageModel
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

@OptIn(ExperimentalPagingApi::class)
class RickAndMortyRemoteMediator(
    private val database: RickAndMortyDatabase,
    private val remoteSource: CharactersRemoteSource,
    private val mapper: CharacterLocalMapper,
    private val filter: CharactersFilterModel?
) : RemoteMediator<Int, CharacterEntity>() {

    override suspend fun initialize(): InitializeAction {
        val hasData = database.charactersDao().countCharacters() > 0
        return if (hasData) {
            InitializeAction.SKIP_INITIAL_REFRESH
        } else {
            InitializeAction.LAUNCH_INITIAL_REFRESH
        }
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, CharacterEntity>
    ): MediatorResult {
        val page = when (val pageOrSuccess = getPageToLoad(loadType, state)) {
            is PageResult.Success -> return MediatorResult.Success(endOfPaginationReached = pageOrSuccess.endOfPaginationReached)
            is PageResult.Page -> pageOrSuccess.page
        }

        return try {
            networkMutex.withLock {
                val response = remoteSource.getCharacters(
                    page = page,
                    name = filter?.name,
                    status = filter?.status,
                    species = filter?.species,
                    type = filter?.type,
                    gender = filter?.gender
                )

                when (response) {
                    is DataResult.Success -> {
                        val endOfPaginationReached = updateLocalDatabase(loadType, response.data)
                        MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
                    }
                    is DataResult.Error -> handleRemoteError(loadType, response.error)
                }
            }
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }

    private suspend fun getPageToLoad(
        loadType: LoadType,
        state: PagingState<Int, CharacterEntity>
    ): PageResult = when (loadType) {
        LoadType.REFRESH -> PageResult.Page(1)
        LoadType.PREPEND -> PageResult.Success(endOfPaginationReached = true)
        LoadType.APPEND -> {
            val remoteKeys = getRemoteKeyForLastItem(state)
            val nextKey = remoteKeys?.nextKey
            if (nextKey == null) {
                PageResult.Success(endOfPaginationReached = remoteKeys != null)
            } else {
                PageResult.Page(nextKey)
            }
        }
    }

    private suspend fun updateLocalDatabase(
        loadType: LoadType,
        pageData: CharactersPageModel
    ): Boolean {
        val endOfPaginationReached = pageData.nextPage == null
        database.withTransaction {
            if (loadType == LoadType.REFRESH) {
                database.remoteKeysDao().clearRemoteKeys()
                database.charactersDao().clearAll()
            }

            val keys = pageData.results.map { character ->
                CharacterRemoteKeysEntity(
                    characterId = character.id,
                    prevKey = pageData.prevPage,
                    nextKey = pageData.nextPage
                )
            }
            database.remoteKeysDao().insertAll(keys)
            database.charactersDao().upsertAll(mapper.toEntityList(pageData.results))
        }
        return endOfPaginationReached
    }

    private suspend fun handleRemoteError(loadType: LoadType, error: AppError): MediatorResult {
        return if (error is AppError.Http && error.code == 404) {
            if (loadType == LoadType.REFRESH) {
                database.withTransaction {
                    database.remoteKeysDao().clearRemoteKeys()
                    database.charactersDao().clearAll()
                }
            }
            MediatorResult.Error(AppError.NoResultsFound)
        } else {
            MediatorResult.Error(Exception(API_ERROR_MESSAGE))
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, CharacterEntity>): CharacterRemoteKeysEntity? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { character ->
                database.remoteKeysDao().remoteKeysByCharacterId(character.id)
            }
    }

    private sealed interface PageResult {
        data class Page(val page: Int) : PageResult
        data class Success(val endOfPaginationReached: Boolean) : PageResult
    }

    companion object {
        private val networkMutex = Mutex()
        private const val API_ERROR_MESSAGE = "API Error"
    }
}
