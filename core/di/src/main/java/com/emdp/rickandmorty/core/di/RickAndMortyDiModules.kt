package com.emdp.rickandmorty.core.di

import com.emdp.rickandmorty.data.di.rickAndMortyNetworkModule
import com.emdp.rickandmorty.data.di.rickAndMortyRemoteModule
import com.emdp.rickandmorty.data.di.rickAndMortyRoomModule
import com.emdp.rickandmorty.domain.di.rickAndMortyDomainModule
import com.emdp.rickandmorty.features.characterslist.di.charactersLisPresentationModule
import org.koin.core.module.Module

object RickAndMortyDiModules {
    fun allModules(): List<Module> = listOf(
        rickAndMortyNetworkModule,
        rickAndMortyRoomModule,
        rickAndMortyRemoteModule,
        rickAndMortyDomainModule,
        charactersLisPresentationModule
    )
}
