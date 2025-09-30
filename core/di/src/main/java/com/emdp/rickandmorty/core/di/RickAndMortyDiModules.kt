package com.emdp.rickandmorty.core.di

import com.emdp.rickandmorty.data.di.rickAndMortyNetworkModule
import org.koin.core.module.Module

object RickAndMortyDiModules {
    fun allModules(): List<Module> = listOf(
        rickAndMortyNetworkModule
    )
}
