package com.emdp.rickandmorty.data.source.remote

import com.emdp.rickandmorty.core.common.result.DataResult
import com.emdp.rickandmorty.domain.models.CharacterModel
import com.emdp.rickandmorty.domain.models.CharactersFilterModel
import com.emdp.rickandmorty.domain.models.RickAndMortyPagedData

interface CharactersRemoteSource {

    suspend fun getCharactersPaged(
        page: Int = 1,
        filter: CharactersFilterModel? = null
    ): DataResult<RickAndMortyPagedData<CharacterModel>>
}