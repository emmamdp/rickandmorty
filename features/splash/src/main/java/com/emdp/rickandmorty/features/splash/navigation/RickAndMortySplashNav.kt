package com.emdp.rickandmorty.features.splash.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.emdp.rickandmorty.core.navigation.RickAndMortyNavRoutes
import com.emdp.rickandmorty.features.splash.presentation.RickAndMortySplashScreen

fun NavGraphBuilder.rickAndMortySplashScreen(onFinished: (() -> Unit)) {
    composable(RickAndMortyNavRoutes.SplashRoute) {
        RickAndMortySplashScreen(onFinished)
    }
}