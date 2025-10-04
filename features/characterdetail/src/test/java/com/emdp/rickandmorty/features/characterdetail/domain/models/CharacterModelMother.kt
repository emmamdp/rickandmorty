package com.emdp.rickandmorty.features.characterdetail.domain.models

import com.emdp.rickandmorty.domain.models.CharacterModel
import com.emdp.rickandmorty.domain.models.enums.CharacterGender
import com.emdp.rickandmorty.domain.models.enums.CharacterStatus

internal object CharacterModelMother {

    private val default = CharacterModel(
        id = 1,
        name = "Rick",
        status = CharacterStatus.ALIVE,
        species = "Human",
        type = "",
        gender = CharacterGender.MALE,
        originName = "Earth (C-137)",
        locationName = "Earth (Replacement Dimension)",
        imageUrl = "https://example.com/rick.png",
        episodeUrls = listOf("", "", ""),
        createdIso = "2017-11-04T18:48:46.250Z"
    )

    fun mockRick() = default

    fun mockMorty() = default.copy(
        id = 2,
        name = "Morty",
        status = CharacterStatus.DEAD,
        species = "Desconocido",
        type = "Desconocido",
        gender = CharacterGender.UNKNOWN,
        originName = "Desconocido",
        locationName = "Desconocido",
        episodeUrls = emptyList()
    )

    fun mockOther() = default.copy(
        id = 3,
        name = "Alien",
        status = CharacterStatus.UNKNOWN,
        species = "Robot"
    )
}