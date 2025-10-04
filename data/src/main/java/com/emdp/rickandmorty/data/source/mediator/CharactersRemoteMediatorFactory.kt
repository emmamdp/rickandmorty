package com.emdp.rickandmorty.data.source.mediator

import com.emdp.rickandmorty.domain.models.CharactersFilterModel

interface CharactersRemoteMediatorFactory {
    fun create(filter: CharactersFilterModel?): CharactersRemoteMediator
}