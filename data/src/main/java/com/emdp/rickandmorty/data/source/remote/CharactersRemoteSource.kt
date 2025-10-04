package com.emdp.rickandmorty.data.source.remote

import com.emdp.rickandmorty.core.common.result.DataResult
import com.emdp.rickandmorty.domain.models.CharactersPageModel

interface CharactersRemoteSource {

    suspend fun getCharacters(
        page: Int?,
        name: String? = null,
        status: String? = null,
        species: String? = null,
        type: String? = null,
        gender: String? = null
    ): DataResult<CharactersPageModel>
}