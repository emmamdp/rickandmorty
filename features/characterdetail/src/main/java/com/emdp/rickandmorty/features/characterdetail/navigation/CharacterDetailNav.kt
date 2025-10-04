package com.emdp.rickandmorty.features.characterdetail.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.emdp.rickandmorty.core.navigation.RickAndMortyNavRoutes
import com.emdp.rickandmorty.features.characterdetail.presentation.CharacterDetailScreen

fun NavGraphBuilder.rickAndMortyCharacterDetailScreen(
    onBackClick: () -> Unit
) {
    composable(
        route = RickAndMortyNavRoutes.CharacterDetail.route,
        arguments = listOf(
            navArgument(RickAndMortyNavRoutes.CharacterDetail.arg) { type = NavType.IntType }
        )
    ) { backStackEntry ->
        val characterId = backStackEntry.arguments?.getInt(
            RickAndMortyNavRoutes.CharacterDetail.arg
        ) ?: return@composable

        CharacterDetailScreen(
            characterId = characterId,
            onBackClick = onBackClick
        )
    }
}