package com.emdp.rickandmorty.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.emdp.rickandmorty.core.common.result.DataResult
import com.emdp.rickandmorty.data.source.local.CharacterLocalSource
import com.emdp.rickandmorty.data.source.local.dao.CharactersDao
import com.emdp.rickandmorty.data.source.local.mapper.CharacterLocalMapper
import com.emdp.rickandmorty.data.source.mediator.CharactersRemoteMediatorFactory
import com.emdp.rickandmorty.domain.models.CharacterModel
import com.emdp.rickandmorty.domain.models.CharactersFilterModel
import com.emdp.rickandmorty.domain.repository.CharactersRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@OptIn(ExperimentalPagingApi::class)
class CharactersRepositoryImpl(
    private val localSource: CharacterLocalSource,
    private val charactersDao: CharactersDao,
    private val mediatorFactory: CharactersRemoteMediatorFactory,
    private val localMapper: CharacterLocalMapper
) : CharactersRepository {

    override fun getCharactersPaged(
        filter: CharactersFilterModel?
    ): Flow<PagingData<CharacterModel>> {

        val mediator = mediatorFactory.create(filter)
        val pagingSourceFactory = {
            charactersDao.pagingSource(
                name = filter?.name,
                status = filter?.status,
                species = filter?.species,
                type = filter?.type,
                gender = filter?.gender
            )
        }

        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                initialLoadSize = PAGE_SIZE,
                prefetchDistance = PREFETCH_DISTANCE,
                enablePlaceholders = ENABLE_PLACEHOLDERS,
                maxSize = PagingConfig.MAX_SIZE_UNBOUNDED
            ),
            remoteMediator = mediator,
            pagingSourceFactory = pagingSourceFactory
        ).flow.map { pagingData ->
            pagingData.map { entity -> localMapper.toModel(entity) }
        }
    }

    override suspend fun getCharacterById(id: Int): DataResult<CharacterModel> =
        localSource.getCharacterById(id)

    companion object {
        private const val PAGE_SIZE = 20
        private const val PREFETCH_DISTANCE = 3
        private const val ENABLE_PLACEHOLDERS = false
    }
}