package com.emdp.rickandmorty.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.emdp.rickandmorty.core.navigation.RickAndMortyNavRoutes
import com.emdp.rickandmorty.features.characterdetail.navigation.rickAndMortyCharacterDetailScreen
import com.emdp.rickandmorty.features.characterslist.navigation.rickAndMortyCharactersListScreen
import com.emdp.rickandmorty.features.splash.navigation.rickAndMortySplashScreen

@Composable
fun RickAndMortyAppNavHost() {
    val nav = rememberNavController()
    NavHost(navController = nav, startDestination = RickAndMortyNavRoutes.SplashRoute) {
        rickAndMortySplashScreen(
            onFinished = {
                nav.navigate(RickAndMortyNavRoutes.CharactersListRoute) {
                    popUpTo(RickAndMortyNavRoutes.SplashRoute) { inclusive = true }
                }
            }
        )

        rickAndMortyCharactersListScreen(
            onCharacterClick = { characterId ->
                nav.navigate(route = RickAndMortyNavRoutes.CharacterDetail.build(characterId)) {
                    launchSingleTop = true
                }
            }
        )

        rickAndMortyCharacterDetailScreen(
            onBackClick = { nav.popBackStack() }
        )
    }
}