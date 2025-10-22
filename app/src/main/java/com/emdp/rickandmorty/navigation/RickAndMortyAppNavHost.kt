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
import com.emdp.rickandmorty.core.navigation.CharacterNavigator
import com.emdp.rickandmorty.core.navigation.RickAndMortyNavRoutes
import com.emdp.rickandmorty.core.ui.background.RickAndMortyGradientBackground
import com.emdp.rickandmorty.features.advancedsearch.navigation.rickAndMortyAdvancedSearchScreen
import com.emdp.rickandmorty.features.characterdetail.navigation.rickAndMortyCharacterDetailScreen
import com.emdp.rickandmorty.features.characterslist.navigation.rickAndMortyCharactersListScreen
import com.emdp.rickandmorty.features.home.navigation.rickAndMortyHomeScreen
import com.emdp.rickandmorty.features.splash.navigation.rickAndMortySplashScreen
import com.emdp.rickandmorty.navigation.bottombar.RickAndMortyBottomBar
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf

@Composable
fun RickAndMortyAppNavHost() {
    val rootNavController = rememberNavController()
    val characterNavigator: CharacterNavigator = koinInject { parametersOf(rootNavController) }

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
                RickAndMortyTabsHostScreen(characterNavigator = characterNavigator)
            }

            rickAndMortyCharacterDetailScreen(
                onBackClick = { rootNavController.popBackStack() }
            )
        }
    }
}

@Composable
private fun RickAndMortyTabsHostScreen(
    characterNavigator: CharacterNavigator
) {
    val tabsNavController = rememberNavController()
    val backStack by tabsNavController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        contentWindowInsets = WindowInsets(left = 0, top = 0, right = 0, bottom = 0),
        bottomBar = {
            RickAndMortyBottomBar(
                currentRoute = currentRoute,
                onNavigate = { route ->
                    tabsNavController.navigateToTab(route)
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
                    tabsNavController.navigateToTab(RickAndMortyNavRoutes.CharactersListRoute)
                },
                onNavigateToSearch = {
                    tabsNavController.navigateToTab(RickAndMortyNavRoutes.AdvancedSearchRoute)
                }
            )

            rickAndMortyCharactersListScreen(
                onCharacterClick = characterNavigator::navigateToDetail
            )

            rickAndMortyAdvancedSearchScreen(
                onCharacterClick = characterNavigator::navigateToDetail
            )
        }
    }
}