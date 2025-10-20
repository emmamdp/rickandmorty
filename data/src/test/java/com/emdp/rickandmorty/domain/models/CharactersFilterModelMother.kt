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

    fun mockRickNull() = default.copy(
        name = "Rick",
        status = null,
        species = null,
        type = null,
        gender = null
    )
}