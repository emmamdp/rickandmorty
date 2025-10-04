package com.emdp.rickandmorty.data.source.mediator

import com.emdp.rickandmorty.data.source.local.RickAndMortyDatabase
import com.emdp.rickandmorty.data.source.local.dao.CharactersDao
import com.emdp.rickandmorty.data.source.local.dao.RemoteKeysDao
import com.emdp.rickandmorty.data.source.local.entity.CharacterEntity
import com.emdp.rickandmorty.data.source.local.mapper.CharacterLocalMapper
import com.emdp.rickandmorty.data.source.remote.CharactersRemoteSource
import com.emdp.rickandmorty.domain.models.CharacterModel
import com.emdp.rickandmorty.domain.models.CharactersFilterModel

class CharactersRemoteMediatorFactoryImpl(
    private val database: RickAndMortyDatabase,
    private val charactersDao: CharactersDao,
    private val remoteKeysDao: RemoteKeysDao,
    private val remoteSource: CharactersRemoteSource,
    private val mapper: CharacterLocalMapper
) : CharactersRemoteMediatorFactory {

    override fun create(filter: CharactersFilterModel?): CharactersRemoteMediator {
        val toEntity: (List<CharacterModel>) -> List<CharacterEntity> = mapper::toEntityList
        return CharactersRemoteMediator(
            database = database,
            charactersDao = charactersDao,
            remoteKeysDao = remoteKeysDao,
            remoteSource = remoteSource,
            toEntity = toEntity,
            filter = filter
        )
    }
}