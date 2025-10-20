package com.emdp.rickandmorty.domain.usecase.characterslist

import com.emdp.rickandmorty.core.common.result.DataResult
import com.emdp.rickandmorty.domain.models.RickAndMortyPagedData
import com.emdp.rickandmorty.domain.models.CharacterModel
import com.emdp.rickandmorty.domain.models.CharactersFilterModel

interface GetCharactersUseCase {

    suspend operator fun invoke(
        page: Int = 1,
        filter: CharactersFilterModel? = null
    ): DataResult<RickAndMortyPagedData<CharacterModel>>
}