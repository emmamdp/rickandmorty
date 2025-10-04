package com.emdp.rickandmorty.data.di

import android.content.Context
import androidx.room.Room
import com.emdp.rickandmorty.data.source.local.CharacterLocalSource
import com.emdp.rickandmorty.data.source.local.CharacterLocalSourceImpl
import com.emdp.rickandmorty.data.source.local.RickAndMortyDatabase
import com.emdp.rickandmorty.data.source.local.mapper.CharacterLocalMapper
import com.emdp.rickandmorty.data.source.local.mapper.CharacterLocalMapperImpl
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val rickAndMortyRoomModule = module {
    single<RickAndMortyDatabase> {
        Room.databaseBuilder(
            context = get<Context>(),
            klass = RickAndMortyDatabase::class.java,
            name = RickAndMortyDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()
    }

    single { get<RickAndMortyDatabase>().charactersDao() }
    single { get<RickAndMortyDatabase>().remoteKeysDao() }

    singleOf(::CharacterLocalMapperImpl) { bind<CharacterLocalMapper>() }
    singleOf(::CharacterLocalSourceImpl) { bind<CharacterLocalSource>() }
}