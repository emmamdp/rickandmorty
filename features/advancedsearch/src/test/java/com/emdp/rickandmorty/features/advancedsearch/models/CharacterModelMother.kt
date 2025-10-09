package com.emdp.rickandmorty.features.advancedsearch.models

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
        originName = "Earth",
        locationName = "Citadel",
        imageUrl = "img",
        episodeUrls = emptyList(),
        createdIso = "2017-11-04T18:48:46.250Z"
    )

    fun mockRick() = default

    fun mockMorty() = default.copy(id = 2, name = "Morty")

    fun mockList() = listOf(
        default,
        default.copy(id = 2, name = "Morty")
    )
}