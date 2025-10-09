package com.emdp.rickandmorty.domain.usecase.advancedsearch

import com.emdp.rickandmorty.core.common.result.DataResult
import com.emdp.rickandmorty.domain.models.CharactersFilterModel
import com.emdp.rickandmorty.domain.models.CharactersPageModel

interface AdvancedSearchUseCase {
    suspend operator fun invoke(
        page: Int,
        filters: CharactersFilterModel
    ): DataResult<CharactersPageModel>
}