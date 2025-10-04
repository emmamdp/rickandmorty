package com.emdp.rickandmorty.domain.usecase.characterslist

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
}