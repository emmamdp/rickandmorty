package com.emdp.rickandmorty.data.source.mediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalPagingApi::class)
class CharactersRemoteMediator(
    private val database: RickAndMortyDatabase,
    private val charactersDao: CharactersDao,
    private val remoteKeysDao: RemoteKeysDao,
    private val remoteSource: CharactersRemoteSource,
    private val toEntity: (List<CharacterModel>) -> List<CharacterEntity>,
    private val filter: CharactersFilterModel?
) : RemoteMediator<Int, CharacterEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, CharacterEntity>
    ): MediatorResult = withContext(Dispatchers.IO) {
        try {
            val page = when (loadType) {
                LoadType.REFRESH -> 1
                LoadType.PREPEND -> {
                    val remoteKeys = getRemoteKeyForFirstItem(state)
                    val prevKey = remoteKeys?.prevKey
                    if (prevKey == null) {
                        return@withContext MediatorResult.Success(endOfPaginationReached = true)
                    }
                    prevKey
                }

                LoadType.APPEND -> {
                    val remoteKeys = getRemoteKeyForLastItem(state)
                    val nextKey = remoteKeys?.nextKey
                    if (nextKey == null) {
                        return@withContext MediatorResult.Success(endOfPaginationReached = true)
                    }
                    nextKey
                }
            }

            val query = filter.toQueryParts()

            when (
                val result = remoteSource.getCharacters(
                    page = page,
                    name = query.name,
                    status = query.status,
                    species = query.species,
                    type = query.type,
                    gender = query.gender
                )
            ) {
                is DataResult.Error -> {
                    return@withContext MediatorResult.Error(
                        RuntimeException("Remote error: ${result.error}")
                    )
                }

                is DataResult.Success -> {
                    val pageModel: CharactersPageModel = result.data
                    val items = pageModel.results
                    val endOfPaginationReached = pageModel.nextPage == null

                    database.withTransaction {
                        if (loadType == LoadType.REFRESH) {
                            remoteKeysDao.clearRemoteKeys()
                            charactersDao.clearAll()
                        }

                        if (items.isNotEmpty()) {
                            val mapped = toEntity(items)

                            val currentMax = charactersDao.maxOrderIndex() ?: -1L
                            val startIndex = when (loadType) {
                                LoadType.REFRESH -> 0L
                                LoadType.APPEND, LoadType.PREPEND -> currentMax + 1L
                            }

                            val entitiesWithOrder = mapped.mapIndexed { i, e ->
                                e.copy(orderIndex = startIndex + i)
                            }

                            charactersDao.upsertAll(entitiesWithOrder)

                            val prevKey = if (page > 1) page - 1 else null
                            val nextKey = if (!endOfPaginationReached) page + 1 else null

                            val keys = entitiesWithOrder.map { e ->
                                RemoteKeysEntity(
                                    characterId = e.id,
                                    prevKey = prevKey,
                                    nextKey = nextKey,
                                    updatedAt = System.currentTimeMillis()
                                )
                            }
                            remoteKeysDao.upsertAll(keys)
                        }
                    }
                    return@withContext MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyForLastItem(
        state: PagingState<Int, CharacterEntity>
    ): RemoteKeysEntity? {
        val lastItem = state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?: charactersDao.lastItem()

        return lastItem?.let { entity ->
            remoteKeysDao.remoteKeysById(entity.id)
        }
    }

    private suspend fun getRemoteKeyForFirstItem(
        state: PagingState<Int, CharacterEntity>
    ): RemoteKeysEntity? {
        val firstItem = state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?: charactersDao.firstItem()

        return firstItem?.let { entity ->
            remoteKeysDao.remoteKeysById(entity.id)
        }
    }

    private fun CharactersFilterModel?.toQueryParts(): QueryParts {
        return QueryParts(
            name = this?.name?.takeIf { it.isNotBlank() },
            status = this?.status?.lowercase()?.takeIf { it.isNotBlank() },
            species = this?.species?.takeIf { it.isNotBlank() },
            type = this?.type?.takeIf { it.isNotBlank() },
            gender = this?.gender?.lowercase()?.takeIf { it.isNotBlank() }
        )
    }

    private data class QueryParts(
        val name: String?,
        val status: String?,
        val species: String?,
        val type: String?,
        val gender: String?
    )
}