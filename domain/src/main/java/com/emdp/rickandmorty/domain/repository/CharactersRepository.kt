package com.emdp.rickandmorty.domain.repository

import com.emdp.rickandmorty.core.common.result.DataResult
import com.emdp.rickandmorty.domain.models.CharacterModel

interface CharactersRepository {

    suspend fun getCharacters(
        page: Int?,
        name: String? = null,
        status: String? = null,
        species: String? = null,
        type: String? = null,
        gender: String? = null
    ): DataResult<List<CharacterModel>>

    suspend fun getCharacterById(id: Int): DataResult<CharacterModel>
}