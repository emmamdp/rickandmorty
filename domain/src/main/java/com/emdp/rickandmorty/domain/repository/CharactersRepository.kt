package com.emdp.rickandmorty.domain.repository

import com.emdp.rickandmorty.core.common.result.DataResult
import com.emdp.rickandmorty.domain.models.CharacterModel
import com.emdp.rickandmorty.domain.models.CharactersFilterModel
import com.emdp.rickandmorty.domain.models.RickAndMortyPagedData

interface CharactersRepository {

    suspend fun getCharactersPaged(
        page: Int = 1,
        filter: CharactersFilterModel? = null
    ): DataResult<RickAndMortyPagedData<CharacterModel>>

    suspend fun getCharacterById(id: Int): DataResult<CharacterModel>
}