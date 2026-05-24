package com.emdp.rickandmorty.data.source.local

import com.emdp.rickandmorty.domain.models.CharacterModel
import kotlinx.coroutines.flow.Flow

interface CharacterLocalSource {
    fun getCharacterById(id: Int): Flow<CharacterModel?>
    suspend fun saveCharacter(character: CharacterModel)
}