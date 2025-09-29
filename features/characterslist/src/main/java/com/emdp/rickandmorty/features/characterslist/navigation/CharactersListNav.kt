package com.emdp.rickandmorty.features.characterslist.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.emdp.rickandmorty.core.navigation.RickAndMortyNavRoutes
import com.emdp.rickandmorty.features.characterslist.presentation.CharactersListScreen

fun NavGraphBuilder.rickAndMortyCharactersListScreen(
    onCharacterClick: (Int) -> Unit
) {
    composable(RickAndMortyNavRoutes.CharactersListRoute) {
        CharactersListScreen(onCharacterClick)
    }
}