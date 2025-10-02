package com.emdp.rickandmorty.domain.usecase.characterslist

import com.emdp.rickandmorty.core.common.result.DataResult
import com.emdp.rickandmorty.domain.models.CharacterModel

interface GetCharactersUseCase {

    suspend operator fun invoke(params: Params): DataResult<List<CharacterModel>>

    data class Params(
        val page: Int? = null,
        val name: String? = null,
        val status: String? = null,
        val species: String? = null,
        val type: String? = null,
        val gender: String? = null
    )
}