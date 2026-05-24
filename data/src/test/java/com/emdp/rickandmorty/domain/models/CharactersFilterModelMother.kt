package com.emdp.rickandmorty.domain.models

internal object CharactersFilterModelMother {

    private val default = CharactersFilterModel(
        name = "Rick",
        status = "Alive",
        species = "Human",
        type = "Scientist",
        gender = "Male"
    )

    fun mock() = default

    fun mockBlank() = default.copy(
        name = " ",
        status = "",
        species = null,
        type = null,
        gender = null
    )
}
