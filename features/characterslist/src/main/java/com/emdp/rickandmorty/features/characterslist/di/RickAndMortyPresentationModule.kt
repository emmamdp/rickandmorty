package com.emdp.rickandmorty.features.characterslist.di

import com.emdp.rickandmorty.features.characterslist.presentation.CharactersListViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val charactersLisPresentationModule = module {
    viewModelOf(::CharactersListViewModel)
}