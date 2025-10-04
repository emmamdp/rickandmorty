package com.emdp.rickandmorty.data.di

import com.emdp.rickandmorty.data.common.network.createService
import com.emdp.rickandmorty.data.repository.CharactersRepositoryImpl
import com.emdp.rickandmorty.data.source.local.entity.CharacterEntity
import com.emdp.rickandmorty.data.source.local.mapper.CharacterLocalMapper
import com.emdp.rickandmorty.data.source.mediator.CharactersRemoteMediator
import com.emdp.rickandmorty.data.source.mediator.CharactersRemoteMediatorFactory
import com.emdp.rickandmorty.data.source.mediator.CharactersRemoteMediatorFactoryImpl
import com.emdp.rickandmorty.data.source.remote.CharactersRemoteSource
import com.emdp.rickandmorty.data.source.remote.CharactersRemoteSourceImpl
import com.emdp.rickandmorty.data.source.remote.api.CharactersApi
import com.emdp.rickandmorty.data.source.remote.mapper.CharactersRemoteMapper
import com.emdp.rickandmorty.data.source.remote.mapper.CharactersRemoteMapperImpl
import com.emdp.rickandmorty.domain.models.CharacterModel
import com.emdp.rickandmorty.domain.repository.CharactersRepository
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import retrofit2.Retrofit

val rickAndMortyRemoteModule = module {

    single<CharactersApi> { get<Retrofit>().createService<CharactersApi>() }

    singleOf(::CharactersRepositoryImpl) { bind<CharactersRepository>() }

    singleOf(::CharactersRemoteMapperImpl) { bind<CharactersRemoteMapper>() }
    singleOf(::CharactersRemoteSourceImpl) { bind<CharactersRemoteSource>() }

    single<(List<CharacterModel>) -> List<CharacterEntity>> {
        get<CharacterLocalMapper>()::toEntityList
    }

    factoryOf(::CharactersRemoteMediator)
    singleOf(::CharactersRemoteMediatorFactoryImpl) { bind<CharactersRemoteMediatorFactory>() }
}