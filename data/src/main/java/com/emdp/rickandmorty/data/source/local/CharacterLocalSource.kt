package com.emdp.rickandmorty.data.source.local

import com.emdp.rickandmorty.core.common.result.DataResult
import com.emdp.rickandmorty.domain.models.CharacterModel
import com.emdp.rickandmorty.domain.models.CharactersFilterModel

interface CharacterLocalSource {

    suspend fun getCharactersPage(
        page: Int,
        pageSize: Int,
        filter: CharactersFilterModel?
    ): List<CharacterModel>

    suspend fun getCharacterById(id: Int): DataResult<CharacterModel>
    suspend fun upsertCharacters(models: List<CharacterModel>)

    suspend fun getTotalPages(filter: CharactersFilterModel?): Int?
    suspend fun saveTotalPages(filter: CharactersFilterModel?, totalPages: Int)
}