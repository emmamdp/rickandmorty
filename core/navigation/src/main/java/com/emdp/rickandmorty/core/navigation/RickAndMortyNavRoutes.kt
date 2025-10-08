package com.emdp.rickandmorty.core.navigation

object RickAndMortyNavRoutes {
    const val HomeRoute = "rickandmorty/home"
    const val SplashRoute = "rickandmorty/splash"
    const val CharactersListRoute = "rickandmorty/characters_list"

    object CharacterDetail {
        const val arg = "characterId"
        const val route = "rickandmorty/detail/{$arg}"
        fun build(characterId: Int) = "rickandmorty/detail/$characterId"
    }
}