package com.emdp.rickandmorty.domain.usecase.character

import com.emdp.rickandmorty.core.common.result.DataResult
import com.emdp.rickandmorty.domain.models.CharacterModel
import com.emdp.rickandmorty.domain.repository.CharactersRepository
import kotlinx.coroutines.flow.Flow

class GetCharacterUseCaseImpl(
    private val repository: CharactersRepository
) : GetCharacterUseCase {

    override fun invoke(
        params: GetCharacterUseCase.Params
    ): Flow<DataResult<CharacterModel>> = repository.getCharacterById(params.id)
}