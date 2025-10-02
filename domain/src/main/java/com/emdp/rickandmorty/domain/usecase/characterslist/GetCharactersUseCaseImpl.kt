package com.emdp.rickandmorty.domain.usecase.characterslist

import com.emdp.rickandmorty.core.common.result.DataResult
import com.emdp.rickandmorty.domain.models.CharactersPageModel
import com.emdp.rickandmorty.domain.repository.CharactersRepository

class GetCharactersUseCaseImpl(
    private val repository: CharactersRepository
) : GetCharactersUseCase {

    override suspend fun invoke(
        params: GetCharactersUseCase.Params
    ): DataResult<CharactersPageModel> = repository.getCharacters(
        page = params.page,
        name = params.name,
        status = params.status,
        species = params.species,
        type = params.type,
        gender = params.gender
    )
}