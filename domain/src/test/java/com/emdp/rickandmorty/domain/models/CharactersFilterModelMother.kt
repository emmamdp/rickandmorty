package com.emdp.rickandmorty.domain.models

internal object CharactersFilterModelMother {

    fun mock() = CharactersFilterModel(
        name = "Rick",
        status = "Alive",
        species = "Human",
        type = "Scientist",
        gender = "Male"
    )

    fun mockEmpty() = CharactersFilterModel(
        name = "",
        status = "",
        species = "",
        type = "",
        gender = ""
    )

    fun mockOnlyName(name: String) = CharactersFilterModel(
        name = name,
        status = null,
        species = null,
        type = null,
        gender = null
    )
}
