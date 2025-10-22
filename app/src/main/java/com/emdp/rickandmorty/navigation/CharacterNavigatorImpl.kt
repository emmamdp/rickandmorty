package com.emdp.rickandmorty.navigation

import androidx.navigation.NavController
import com.emdp.rickandmorty.core.navigation.CharacterNavigator
import com.emdp.rickandmorty.core.navigation.RickAndMortyNavRoutes

class CharacterNavigatorImpl(
    private val navController: NavController
) : CharacterNavigator {

    override fun navigateToDetail(characterId: Int) {
        navController.navigate(
            RickAndMortyNavRoutes.CharacterDetail.build(characterId)
        ) {
            launchSingleTop = true
        }
    }
}