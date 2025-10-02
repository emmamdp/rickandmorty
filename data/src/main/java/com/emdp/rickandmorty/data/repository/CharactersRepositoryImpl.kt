package com.emdp.rickandmorty.data.repository

import com.emdp.rickandmorty.core.common.result.DataResult
import com.emdp.rickandmorty.data.source.remote.CharactersRemoteSource
import com.emdp.rickandmorty.domain.models.CharacterModel
import com.emdp.rickandmorty.domain.models.CharactersPageModel
import com.emdp.rickandmorty.domain.repository.CharactersRepository

class CharactersRepositoryImpl(
    private val remoteSource: CharactersRemoteSource
) : CharactersRepository {

    override suspend fun getCharacters(
        page: Int?,
        name: String?,
        status: String?,
        species: String?,
        type: String?,
        gender: String?
    ): DataResult<CharactersPageModel> =
        remoteSource.getCharacters(page, name, status, species, type, gender)

    override suspend fun getCharacterById(id: Int): DataResult<CharacterModel> =
        remoteSource.getCharacterById(id)
}