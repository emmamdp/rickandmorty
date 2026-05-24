package com.emdp.rickandmorty.data.source.remote

import com.emdp.rickandmorty.core.common.result.DataResult
import com.emdp.rickandmorty.data.source.remote.api.CharactersApi
import com.emdp.rickandmorty.data.source.remote.mapper.CharactersRemoteMapper
import com.emdp.rickandmorty.domain.models.CharacterModel
import com.emdp.rickandmorty.domain.models.CharactersPageModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

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
    ): DataResult<CharactersPageModel> = try {
        val response = api.getCharacters(page, name, status, species, type, gender)
        DataResult.Success(data = mapper.toModel(response))
    } catch (throwable: Throwable) {
        if (throwable is kotlinx.coroutines.CancellationException) throw throwable
        DataResult.Error(error = mapper.toError(throwable))
    }

    override fun getCharacterById(id: Int): Flow<DataResult<CharacterModel>> = flow<DataResult<CharacterModel>> {
        val response = api.getCharacterById(id)
        emit(DataResult.Success(data = mapper.toModel(response)))
    }.catch { throwable ->
        emit(DataResult.Error(error = mapper.toError(throwable)))
    }
}