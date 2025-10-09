package com.emdp.rickandmorty.features.advancedsearch.models

import com.emdp.rickandmorty.domain.models.CharactersPageModel

internal object CharactersPageModelMother {

    private val default = CharactersPageModel(
        count = 826,
        pages = 42,
        nextPage = 2,
        prevPage = null,
        results = CharacterModelMother.mockList()
    )

    fun mock() = default
}