package com.emdp.rickandmorty.domain.models

internal object CharactersFilterModelMother {

    private val default = CharactersFilterModel(
        name = "Rick",
        status = "alive",
        species = "Human",
        type = "",
        gender = "male"
    )

    fun mock() = default

    fun mockEmpty() = CharactersFilterModel(
        name = null,
        status = null,
        species = null,
        type = null,
        gender = null
    )

    fun mockOnlyName(name: String = "Morty") = CharactersFilterModel(
        name = name,
        status = null,
        species = null,
        type = null,
        gender = null
    )
}