package com.emdp.rickandmorty.data.source.mediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.emdp.rickandmorty.core.common.result.AppError
import com.emdp.rickandmorty.core.common.result.DataResult
import com.emdp.rickandmorty.data.source.local.RickAndMortyDatabase
import com.emdp.rickandmorty.data.source.local.dao.CharactersDao
import com.emdp.rickandmorty.data.source.local.dao.RemoteKeysDao
import com.emdp.rickandmorty.data.source.local.entity.CharacterEntity
import com.emdp.rickandmorty.data.source.local.entity.RemoteKeysEntity
import com.emdp.rickandmorty.data.source.remote.CharactersRemoteSource
import com.emdp.rickandmorty.domain.models.CharacterModel
import com.emdp.rickandmorty.domain.models.CharactersFilterModel
import com.emdp.rickandmorty.domain.models.CharactersPageModel
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class CharactersRemoteMediator(
    private val database: RickAndMortyDatabase,
    private val charactersDao: CharactersDao,
    private val remoteKeysDao: RemoteKeysDao,
    private val remoteSource: CharactersRemoteSource,
    private val toEntity: (List<CharacterModel>) -> List<CharacterEntity>,
    private val filter: CharactersFilterModel?,
    private val runInTransaction: suspend (suspend () -> Unit) -> Unit = { block ->
        database.withTransaction { block() }
    }
) : RemoteMediator<Int, CharacterEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, CharacterEntity>
    ): MediatorResult = try {

        val page = when (loadType) {
            LoadType.REFRESH -> getRemoteKeyClosestToCurrentPosition(state)?.nextKey?.minus(1) ?: 1
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey
                if (prevKey == null) return MediatorResult.Success(endOfPaginationReached = true)
                prevKey
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                if (nextKey == null) return MediatorResult.Success(endOfPaginationReached = true)
                nextKey
            }
        }

        val pageResult: CharactersPageModel = fetchPage(page)
        val items = pageResult.results
        val endOfPaginationReached = items.isEmpty() || page >= pageResult.pages
        val prevKey = if (page > 1) page - 1 else null
        val nextKey = if (!endOfPaginationReached) page + 1 else null

        persistPage(loadType, items, prevKey, nextKey)

        MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
    } catch (e: Exception) {
        MediatorResult.Error(e)
    }

    private suspend fun fetchPage(page: Int): CharactersPageModel =
        when (val result = remoteSource.getCharacters(
            page = page,
            name = filter?.name,
            status = filter?.status,
            species = filter?.species,
            type = filter?.type,
            gender = filter?.gender
        )) {
            is DataResult.Success -> result.data
            is DataResult.Error   -> throw mapAppErrorToException(result.error)
        }

    private suspend fun persistPage(
        loadType: LoadType,
        items: List<CharacterModel>,
        prevKey: Int?,
        nextKey: Int?
    ) {
        runInTransaction {
            if (loadType == LoadType.REFRESH) {
                remoteKeysDao.clearRemoteKeys()
                charactersDao.clearAll()
            }

            val entities = toEntity(items)
            charactersDao.upsertAll(entities)

            val now = System.currentTimeMillis()
            val keys = items.map { c ->
                RemoteKeysEntity(
                    characterId = c.id,
                    prevKey = prevKey,
                    nextKey = nextKey,
                    updatedAt = now
                )
            }
            remoteKeysDao.upsertAll(keys)
        }
    }

    private suspend fun getRemoteKeyForLastItem(
        state: PagingState<Int, CharacterEntity>
    ): RemoteKeysEntity? =
        state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { entity -> remoteKeysDao.remoteKeysById(entity.id) }

    private suspend fun getRemoteKeyForFirstItem(
        state: PagingState<Int, CharacterEntity>
    ): RemoteKeysEntity? =
        state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { entity -> remoteKeysDao.remoteKeysById(entity.id) }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, CharacterEntity>
    ): RemoteKeysEntity? =
        state.anchorPosition?.let { pos -> state.closestItemToPosition(pos)?.id }
            ?.let { id -> remoteKeysDao.remoteKeysById(id) }

    private fun mapAppErrorToException(error: AppError): Exception =
        when (error) {
            is AppError.Network       -> IOException("Network error")
            is AppError.Serialization -> IOException("Serialization error")
            is AppError.Unexpected    -> RuntimeException(Throwable("Unknown error"))
            else -> RuntimeException(error.toString())
        }
}