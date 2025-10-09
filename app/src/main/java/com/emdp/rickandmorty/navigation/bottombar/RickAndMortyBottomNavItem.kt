package com.emdp.rickandmorty.navigation.bottombar

import androidx.annotation.DrawableRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector
import com.emdp.rickandmorty.R
import com.emdp.rickandmorty.core.navigation.RickAndMortyNavRoutes

sealed interface RickAndMortyBottomNavItem {
    val route: String
    val labelResId: Int

    data class VectorNavItem(
        override val route: String,
        override val labelResId: Int,
        val icon: ImageVector
    ) : RickAndMortyBottomNavItem

    data class PainterNavItem(
        override val route: String,
        override val labelResId: Int,
        @get:DrawableRes val painterResId: Int
    ) : RickAndMortyBottomNavItem
}

internal fun bottomItems(): List<RickAndMortyBottomNavItem> = listOf(
    RickAndMortyBottomNavItem.VectorNavItem(
        route = RickAndMortyNavRoutes.HomeRoute,
        labelResId = R.string.nav_home,
        icon = Icons.Default.Home
    ),
    RickAndMortyBottomNavItem.PainterNavItem(
        route = RickAndMortyNavRoutes.CharactersListRoute,
        labelResId = R.string.nav_characters,
        painterResId = R.drawable.ic_characters_portal
    ),
    RickAndMortyBottomNavItem.VectorNavItem(
        route = RickAndMortyNavRoutes.AdvancedSearchRoute,
        labelResId = R.string.nav_search,
        icon = Icons.Default.Search
    )
)