package com.emdp.rickandmorty.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.emdp.rickandmorty.core.common.result.DataResult
import com.emdp.rickandmorty.data.source.local.CharacterLocalSource
import com.emdp.rickandmorty.data.source.local.dao.CharactersDao
import com.emdp.rickandmorty.data.source.local.entity.CharacterEntity
import com.emdp.rickandmorty.data.source.paging.RickAndMortyPagingSource
import com.emdp.rickandmorty.data.source.remote.CharactersRemoteSource
import com.emdp.rickandmorty.domain.models.CharacterModel
import com.emdp.rickandmorty.domain.models.CharactersFilterModel
import com.emdp.rickandmorty.domain.models.CharactersPageModel
import com.emdp.rickandmorty.domain.repository.CharactersRepository
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalPagingApi::class)
class CharactersRepositoryImpl(
    private val localSource: CharacterLocalSource,
    private val charactersDao: CharactersDao,
    private val remoteSource: CharactersRemoteSource,
    private val toEntity: (List<CharacterModel>) -> List<CharacterEntity>
) : CharactersRepository {

    override fun getCharactersPaged(
        filter: CharactersFilterModel?
    ): Flow<PagingData<CharacterModel>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                initialLoadSize = 20,
                prefetchDistance = 3,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                RickAndMortyPagingSource(
                    remoteSource = remoteSource,
                    charactersDao = charactersDao,
                    toEntity = toEntity,
                    filter = filter
                )
            }
        ).flow
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