package com.emdp.rickandmorty.data.source.remote

import com.emdp.rickandmorty.core.common.result.DataResult
import com.emdp.rickandmorty.data.source.remote.api.CharactersApi
import com.emdp.rickandmorty.data.source.remote.mapper.CharactersRemoteMapper
import com.emdp.rickandmorty.domain.models.CharacterModel
import com.emdp.rickandmorty.domain.models.CharactersFilterModel
import com.emdp.rickandmorty.domain.models.RickAndMortyPagedData

class CharactersRemoteSourceImpl(
    private val api: CharactersApi,
    private val mapper: CharactersRemoteMapper
) : CharactersRemoteSource {

    override suspend fun getCharactersPaged(
        page: Int,
        filter: CharactersFilterModel?
    ): DataResult<RickAndMortyPagedData<CharacterModel>> = runCatching {
        val response = api.getCharacters(
            page = page,
            name = filter?.name,
            status = filter?.status,
            species = filter?.species,
            type = filter?.type,
            gender = filter?.gender
        )
        val model = mapper.toModel(response = response, requestedPage = page)
        DataResult.Success(data = model)
    }.getOrElse { throwable ->
        DataResult.Error(error = mapper.toError(throwable))
    }
}