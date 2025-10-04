package com.emdp.rickandmorty.features.characterslist.domain.models

import com.emdp.rickandmorty.domain.models.CharactersFilterModel

internal object CharactersFilterModelMother {

    private val default = CharactersFilterModel(
        name = "Rick",
        status = "Alive",
        species = "Human",
        type = null,
        gender = "Male"
    )

    fun mock() = default

    fun mockMortyWithNulls() = default.copy(
        name = "Morty",
        status = null,
        species = null,
        type = null,
        gender = null
    )
}