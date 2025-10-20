package com.emdp.rickandmorty.domain.usecase.characterslist

import com.emdp.rickandmorty.core.common.result.DataResult
import com.emdp.rickandmorty.domain.models.CharacterModel
import com.emdp.rickandmorty.domain.models.CharactersFilterModel
import com.emdp.rickandmorty.domain.models.RickAndMortyPagedData
import com.emdp.rickandmorty.domain.repository.CharactersRepository

class GetCharactersUseCaseImpl(
    private val repository: CharactersRepository
) : GetCharactersUseCase {

    override suspend fun invoke(
        page: Int,
        filter: CharactersFilterModel?
    ): DataResult<RickAndMortyPagedData<CharacterModel>> = repository.getCharactersPaged(page, filter)
}