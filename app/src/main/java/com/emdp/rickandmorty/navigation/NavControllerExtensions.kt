package com.emdp.rickandmorty.navigation

import androidx.navigation.NavController
import com.emdp.rickandmorty.core.navigation.RickAndMortyNavRoutes

fun NavController.navigateToTab(route: String) {
    navigate(route) {
        popUpTo(RickAndMortyNavRoutes.HomeRoute) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}