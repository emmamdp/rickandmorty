package com.emdp.rickandmorty.core.navigation

object RickAndMortyNavRoutes {
    const val TabsRoute = "tabs"
    const val HomeRoute = "rickandmorty/home"
    const val SplashRoute = "rickandmorty/splash"
    const val CharactersListRoute = "rickandmorty/characters_list"
    const val AdvancedSearchRoute = "rickandmorty/advanced_search"

    object CharacterDetail {
        const val arg = "characterId"
        const val route = "rickandmorty/detail/{$arg}"
        fun build(characterId: Int) = "rickandmorty/detail/$characterId"
    }
}