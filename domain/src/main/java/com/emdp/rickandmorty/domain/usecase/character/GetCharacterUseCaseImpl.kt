package com.emdp.rickandmorty.domain.usecase.character

import com.emdp.rickandmorty.core.common.result.DataResult
import com.emdp.rickandmorty.domain.models.CharacterModel
import com.emdp.rickandmorty.domain.repository.CharactersRepository

class GetCharacterUseCaseImpl(
    private val repository: CharactersRepository
) : GetCharacterUseCase {

    override suspend fun invoke(
        params: GetCharacterUseCase.Params
    ): DataResult<CharacterModel> = repository.getCharacterById(params.id)
}