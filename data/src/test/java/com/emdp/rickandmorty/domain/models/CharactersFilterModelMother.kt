package com.emdp.rickandmorty.domain.models

internal object CharactersFilterModelMother {

    private val default = CharactersFilterModel(
        name = "Rick",
        status = "Alive",
        species = "Human",
        type = null,
        gender = "Male"
    )

    fun mock() = default

    fun mockNull() = CharactersFilterModel(
        name = null,
        status = null,
        species = null,
        type = null,
        gender = null
    )
}