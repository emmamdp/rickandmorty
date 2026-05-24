package com.emdp.rickandmorty.data.source.local

import com.emdp.rickandmorty.data.source.local.dao.CharactersDao
import com.emdp.rickandmorty.data.source.local.mapper.CharacterLocalMapper
import com.emdp.rickandmorty.domain.models.CharacterModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CharacterLocalSourceImpl(
    private val charactersDao: CharactersDao,
    private val localMapper: CharacterLocalMapper
) : CharacterLocalSource {

    override fun getCharacterById(id: Int): Flow<CharacterModel?> =
        charactersDao.observeCharacterById(id).map { entity ->
            entity?.let { localMapper.toModel(it) }
        }

    override suspend fun saveCharacter(character: CharacterModel) {
        charactersDao.upsertAll(listOf(localMapper.toEntity(character)))
    }
}