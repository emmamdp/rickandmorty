package com.emdp.rickandmorty.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.emdp.rickandmorty.core.navigation.RickAndMortyNavRoutes
import com.emdp.rickandmorty.core.ui.background.RickAndMortyGradientBackground
import com.emdp.rickandmorty.features.characterdetail.navigation.rickAndMortyCharacterDetailScreen
import com.emdp.rickandmorty.features.characterslist.navigation.rickAndMortyCharactersListScreen
import com.emdp.rickandmorty.features.home.navigation.rickAndMortyHomeScreen
import com.emdp.rickandmorty.features.splash.navigation.rickAndMortySplashScreen
import com.emdp.rickandmorty.navigation.bottombar.RickAndMortyBottomBar

@Composable
fun RickAndMortyAppNavHost() {
    val rootNavController = rememberNavController()

    RickAndMortyGradientBackground {
        NavHost(
            navController = rootNavController,
            startDestination = RickAndMortyNavRoutes.SplashRoute,
            modifier = Modifier.fillMaxSize()
        ) {
            rickAndMortySplashScreen(
                onFinished = {
                    rootNavController.navigate(RickAndMortyNavRoutes.TabsRoute) {
                        popUpTo(RickAndMortyNavRoutes.SplashRoute) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )

            composable(RickAndMortyNavRoutes.TabsRoute) {
                RickAndMortyTabsHostScreen(
                    onNavigateToDetail = { characterId ->
                        rootNavController.navigate(
                            RickAndMortyNavRoutes.CharacterDetail.build(characterId)
                        ) {
                            launchSingleTop = true
                        }
                    }
                )
            }

            rickAndMortyCharacterDetailScreen(
                onBackClick = { rootNavController.popBackStack() }
            )
        }
    }
}

@Composable
private fun RickAndMortyTabsHostScreen(
    onNavigateToDetail: (Int) -> Unit
) {
    val tabsNavController = rememberNavController()
    val backStack by tabsNavController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            RickAndMortyBottomBar(
                currentRoute = currentRoute,
                onNavigate = { route ->
                    tabsNavController.navigate(route) {
                        popUpTo(RickAndMortyNavRoutes.HomeRoute) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    ) { paddingValues ->
        NavHost(
            navController = tabsNavController,
            startDestination = RickAndMortyNavRoutes.HomeRoute,
            modifier = Modifier.padding(paddingValues)
        ) {
            rickAndMortyHomeScreen(
                onNavigateToCharacters = {
                    tabsNavController.navigate(RickAndMortyNavRoutes.CharactersListRoute) {
                        launchSingleTop = true
                        restoreState = true
                        popUpTo(RickAndMortyNavRoutes.HomeRoute) { saveState = true }
                    }
                },
                onNavigateToSearch = {
                    tabsNavController.navigate(RickAndMortyNavRoutes.AdvancedSearchRoute) {
                        launchSingleTop = true
                        restoreState = true
                        popUpTo(RickAndMortyNavRoutes.HomeRoute) { saveState = true }
                    }
                }
            )

            rickAndMortyCharactersListScreen(
                onCharacterClick = { id -> onNavigateToDetail(id) }
            )

            composable(RickAndMortyNavRoutes.AdvancedSearchRoute) { }
        }
    }
}