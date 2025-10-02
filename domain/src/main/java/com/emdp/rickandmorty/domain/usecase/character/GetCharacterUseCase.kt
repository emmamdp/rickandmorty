package com.emdp.rickandmorty.domain.usecase.character

import com.emdp.rickandmorty.core.common.result.DataResult
import com.emdp.rickandmorty.domain.models.CharacterModel

interface GetCharacterUseCase {

    suspend operator fun invoke(params: Params): DataResult<CharacterModel>

    data class Params(val id: Int)
}