package com.emdp.rickandmorty.features.advancedsearch.di

import com.emdp.rickandmorty.features.advancedsearch.presentation.RickAndMortyAdvancedSearchViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val rickAndMortyAdvancedSearchModule = module {
    viewModelOf(::RickAndMortyAdvancedSearchViewModel)
}