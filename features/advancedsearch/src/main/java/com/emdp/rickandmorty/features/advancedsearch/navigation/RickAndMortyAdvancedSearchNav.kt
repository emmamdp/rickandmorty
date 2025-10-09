package com.emdp.rickandmorty.features.advancedsearch.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.emdp.rickandmorty.core.navigation.RickAndMortyNavRoutes
import com.emdp.rickandmorty.features.advancedsearch.presentation.RickAndMortyAdvancedSearchScreen

fun NavGraphBuilder.rickAndMortyAdvancedSearchScreen(
    onCharacterClick: (Int) -> Unit
) {
    composable(RickAndMortyNavRoutes.AdvancedSearchRoute) {
        RickAndMortyAdvancedSearchScreen(onCharacterClick)
    }
}