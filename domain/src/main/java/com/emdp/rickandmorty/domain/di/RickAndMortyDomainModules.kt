package com.emdp.rickandmorty.domain.di

import com.emdp.rickandmorty.domain.usecase.character.GetCharacterUseCase
import com.emdp.rickandmorty.domain.usecase.character.GetCharacterUseCaseImpl
import com.emdp.rickandmorty.domain.usecase.characterslist.GetCharactersUseCase
import com.emdp.rickandmorty.domain.usecase.characterslist.GetCharactersUseCaseImpl
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val rickAndMortyDomainModule = module {
    factoryOf(::GetCharactersUseCaseImpl) { bind<GetCharactersUseCase>() }
    factoryOf(::GetCharacterUseCaseImpl) { bind<GetCharacterUseCase>() }
}