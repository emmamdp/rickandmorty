package com.emdp.rickandmorty.domain.models

internal object CharactersPageModelMother {

    private val default = CharactersPageModel(
        count = 826,
        pages = 42,
        nextPage = 2,
        prevPage = null,
        results = CharacterModelMother.mockList()
    )

    fun mock() = default

    fun mockEmpty() = default.copy(
        count = 0,
        pages = 0,
        nextPage = null,
        prevPage = null,
        results = emptyList()
    )
}