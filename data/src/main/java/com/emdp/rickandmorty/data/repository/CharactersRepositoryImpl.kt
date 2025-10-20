package com.emdp.rickandmorty.data.repository

import com.emdp.rickandmorty.core.common.result.DataResult
import com.emdp.rickandmorty.data.source.local.CharacterLocalSource
import com.emdp.rickandmorty.data.source.remote.CharactersRemoteSource
import com.emdp.rickandmorty.domain.models.CharacterModel
import com.emdp.rickandmorty.domain.models.CharactersFilterModel
import com.emdp.rickandmorty.domain.models.RickAndMortyPagedData
import com.emdp.rickandmorty.domain.repository.CharactersRepository

class CharactersRepositoryImpl(
    private val localSource: CharacterLocalSource,
    private val remoteSource: CharactersRemoteSource
) : CharactersRepository {

    override suspend fun getCharactersPaged(
        page: Int,
        filter: CharactersFilterModel?
    ): DataResult<RickAndMortyPagedData<CharacterModel>> {

        val localPage = localSource.getCharactersPage(page, PAGE_SIZE, filter)
        val isFullPage = localPage.size == PAGE_SIZE
        var data: List<CharacterModel> = localPage
        var hasMore: Boolean
        var totalPages: Int?

        if (!isFullPage) {
            when (val remote = remoteSource.getCharactersPaged(page, filter)) {
                is DataResult.Success -> {
                    localSource.upsertCharacters(remote.data.data)
                    remote.data.totalPages?.let { localSource.saveTotalPages(filter, it) }

                    data = remote.data.data
                    hasMore = remote.data.hasMore
                    totalPages = remote.data.totalPages
                }

                is DataResult.Error -> {
                    if (localPage.isEmpty()) {
                        return DataResult.Error(remote.error)
                    }
                    totalPages = localSource.getTotalPages(filter)
                    hasMore = totalPages?.let { page < it } ?: (localPage.size == PAGE_SIZE)
                    data = localPage
                }
            }
        } else {
            totalPages = localSource.getTotalPages(filter)
            hasMore = totalPages?.let { page < it } ?: true
        }

        return DataResult.Success(
            RickAndMortyPagedData(
                data = data,
                page = page,
                hasMore = hasMore,
                totalPages = totalPages
            )
        )
    }

    override suspend fun getCharacterById(id: Int): DataResult<CharacterModel> =
        localSource.getCharacterById(id)

    companion object {
        private const val PAGE_SIZE = 20
    }
}