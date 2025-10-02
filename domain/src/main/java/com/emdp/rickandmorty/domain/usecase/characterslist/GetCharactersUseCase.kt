package com.emdp.rickandmorty.domain.usecase.characterslist

import com.emdp.rickandmorty.core.common.result.DataResult
import com.emdp.rickandmorty.domain.models.CharactersPageModel

interface GetCharactersUseCase {

    suspend operator fun invoke(params: Params): DataResult<CharactersPageModel>

    data class Params(
        val page: Int? = null,
        val name: String? = null,
        val status: String? = null,
        val species: String? = null,
        val type: String? = null,
        val gender: String? = null
    )
}