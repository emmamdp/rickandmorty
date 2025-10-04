package com.emdp.rickandmorty.features.characterdetail.di

import com.emdp.rickandmorty.features.characterdetail.presentation.CharacterDetailViewModel
import com.emdp.rickandmorty.features.characterdetail.presentation.mapper.CharacterDetailMapper
import com.emdp.rickandmorty.features.characterdetail.presentation.mapper.CharacterDetailMapperImpl
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val characterDetailPresentationModule = module {
    single<CharacterDetailMapper> { CharacterDetailMapperImpl(androidContext().resources) }
    viewModelOf(::CharacterDetailViewModel)
}