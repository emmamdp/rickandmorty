package com.emdp.rickandmorty.navigation.di

import androidx.navigation.NavHostController
import com.emdp.rickandmorty.core.navigation.CharacterNavigator
import com.emdp.rickandmorty.navigation.CharacterNavigatorImpl
import org.koin.dsl.module

val navigationModule = module {
    factory<CharacterNavigator> { (navController: NavHostController) ->
        CharacterNavigatorImpl(navController)
    }
}