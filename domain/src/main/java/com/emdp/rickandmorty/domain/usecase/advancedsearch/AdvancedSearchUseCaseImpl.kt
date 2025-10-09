package com.emdp.rickandmorty.domain.usecase.advancedsearch

import com.emdp.rickandmorty.core.common.result.DataResult
import com.emdp.rickandmorty.domain.models.CharactersFilterModel
import com.emdp.rickandmorty.domain.models.CharactersPageModel
import com.emdp.rickandmorty.domain.repository.CharactersRepository

class AdvancedSearchUseCaseImpl(
    private val repository: CharactersRepository
) : AdvancedSearchUseCase {

    override suspend fun invoke(
        page: Int,
        filters: CharactersFilterModel
    ): DataResult<CharactersPageModel> = repository.searchCharacters(page = page, filters = filters)
}