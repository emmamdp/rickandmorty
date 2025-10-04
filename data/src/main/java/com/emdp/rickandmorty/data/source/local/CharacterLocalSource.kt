package com.emdp.rickandmorty.data.source.local

import com.emdp.rickandmorty.core.common.result.DataResult
import com.emdp.rickandmorty.domain.models.CharacterModel

interface CharacterLocalSource {
    suspend fun getCharacterById(id: Int): DataResult<CharacterModel>
}