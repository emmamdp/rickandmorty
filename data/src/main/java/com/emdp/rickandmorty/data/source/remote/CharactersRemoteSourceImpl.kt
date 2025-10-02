package com.emdp.rickandmorty.data.source.remote

import com.emdp.rickandmorty.core.common.result.DataResult
import com.emdp.rickandmorty.data.source.remote.api.CharactersApi
import com.emdp.rickandmorty.data.source.remote.mapper.CharactersRemoteMapper
import com.emdp.rickandmorty.domain.models.CharacterModel

class CharactersRemoteSourceImpl(
    private val api: CharactersApi,
    private val mapper: CharactersRemoteMapper
) : CharactersRemoteSource {

    override suspend fun getCharacters(
        page: Int?,
        name: String?,
        status: String?,
        species: String?,
        type: String?,
        gender: String?
    ): DataResult<List<CharacterModel>> = runCatching {
        val response = api.getCharacters(page, name, status, species, type, gender)
        DataResult.Success(data = mapper.toModel(response))
    }.getOrElse { throwable ->
        DataResult.Error(error = mapper.toError(throwable))
    }

    override suspend fun getCharacterById(id: Int): DataResult<CharacterModel> =
        runCatching {
            val dto = api.getCharacterById(id)
            DataResult.Success(data = mapper.toModel(dto))
        }.getOrElse { throwable ->
            DataResult.Error(error = mapper.toError(throwable))
        }
}