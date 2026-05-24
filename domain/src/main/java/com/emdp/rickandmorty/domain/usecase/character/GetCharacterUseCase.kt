package com.emdp.rickandmorty.domain.usecase.character

import com.emdp.rickandmorty.core.common.result.DataResult
import com.emdp.rickandmorty.domain.models.CharacterModel
import kotlinx.coroutines.flow.Flow

interface GetCharacterUseCase {

    operator fun invoke(params: Params): Flow<DataResult<CharacterModel>>

    data class Params(val id: Int)
}