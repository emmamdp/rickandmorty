package com.emdp.rickandmorty.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.emdp.rickandmorty.core.common.result.DataResult
import com.emdp.rickandmorty.data.source.local.CharacterLocalSource
import com.emdp.rickandmorty.data.source.local.RickAndMortyDatabase
import com.emdp.rickandmorty.data.source.local.dao.CharactersDao
import com.emdp.rickandmorty.data.source.local.mapper.CharacterLocalMapper
import com.emdp.rickandmorty.data.source.paging.RickAndMortyRemoteMediator
import com.emdp.rickandmorty.data.source.remote.CharactersRemoteSource
import com.emdp.rickandmorty.domain.models.CharacterModel
import com.emdp.rickandmorty.domain.models.CharactersFilterModel
import com.emdp.rickandmorty.domain.models.CharactersPageModel
import com.emdp.rickandmorty.domain.repository.CharactersRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@OptIn(ExperimentalPagingApi::class)
class CharactersRepositoryImpl(
    private val database: RickAndMortyDatabase,
    private val localSource: CharacterLocalSource,
    private val charactersDao: CharactersDao,
    private val remoteSource: CharactersRemoteSource,
    private val mapper: CharacterLocalMapper
) : CharactersRepository {

    override fun getCharactersPaged(
        filter: CharactersFilterModel?
    ): Flow<PagingData<CharacterModel>> {
        val queryName = filter?.name?.takeIf { it.isNotBlank() }
        val queryStatus = filter?.status?.lowercase()?.takeIf { it.isNotBlank() }
        val querySpecies = filter?.species?.takeIf { it.isNotBlank() }
        val queryType = filter?.type?.takeIf { it.isNotBlank() }
        val queryGender = filter?.gender?.lowercase()?.takeIf { it.isNotBlank() }

        return Pager(
            config = PagingConfig(
                pageSize = 20,
                initialLoadSize = 40,
                prefetchDistance = 10,
                enablePlaceholders = false
            ),
            remoteMediator = RickAndMortyRemoteMediator(
                database = database,
                remoteSource = remoteSource,
                mapper = mapper,
                filter = filter
            ),
            pagingSourceFactory = {
                charactersDao.pagingSource(
                    name = queryName,
                    status = queryStatus,
                    species = querySpecies,
                    type = queryType,
                    gender = queryGender
                )
            }
        ).flow.map { pagingData ->
            pagingData.map { entity -> mapper.toModel(entity) }
        }
    }

    override suspend fun searchCharacters(
        page: Int,
        filters: CharactersFilterModel
    ): DataResult<CharactersPageModel> = remoteSource.getCharacters(
        page = page,
        name = filters.name,
        status = filters.status,
        species = filters.species,
        type = filters.type,
        gender = filters.gender
    )

    override suspend fun getCharacterById(id: Int): DataResult<CharacterModel> =
        localSource.getCharacterById(id)
}
