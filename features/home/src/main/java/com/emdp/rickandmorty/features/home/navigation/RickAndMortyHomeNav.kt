package com.emdp.rickandmorty.features.home.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.emdp.rickandmorty.core.navigation.RickAndMortyNavRoutes
import com.emdp.rickandmorty.features.home.presentation.RickAndMortyHomeScreen

fun NavGraphBuilder.rickAndMortyHomeScreen(
    onNavigateToCharacters: () -> Unit,
    onNavigateToSearch: () -> Unit
) {
    composable(RickAndMortyNavRoutes.HomeRoute) {
        RickAndMortyHomeScreen(onNavigateToCharacters, onNavigateToSearch)
    }
}