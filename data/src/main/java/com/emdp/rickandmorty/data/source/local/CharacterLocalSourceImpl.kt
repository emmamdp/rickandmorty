package com.emdp.rickandmorty.data.source.local

import com.emdp.rickandmorty.core.common.result.AppError
import com.emdp.rickandmorty.core.common.result.DataResult
import com.emdp.rickandmorty.data.source.local.dao.CharactersDao
import com.emdp.rickandmorty.data.source.local.mapper.CharacterLocalMapper
import com.emdp.rickandmorty.domain.models.CharacterModel

class CharacterLocalSourceImpl(
    private val charactersDao: CharactersDao,
    private val localMapper: CharacterLocalMapper
) : CharacterLocalSource {

    override suspend fun getCharacterById(id: Int): DataResult<CharacterModel> =
        try {
            val entity = charactersDao.getCharacterById(id)
            if (entity != null) {
                DataResult.Success(data = localMapper.toModel(entity))
            } else {
                DataResult.Error(error = AppError.DataNotFound)
            }
        } catch (t: Throwable) {
            DataResult.Error(error = AppError.Unexpected(t))
        }
}