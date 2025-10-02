package com.emdp.rickandmorty.domain.models

internal object CharacterModelMother {

    private val default = CharacterModel(
        id = 1,
        name = "Rick",
        status = com.emdp.rickandmorty.domain.models.enums.CharacterStatus.ALIVE,
        species = "Human",
        type = "",
        gender = com.emdp.rickandmorty.domain.models.enums.CharacterGender.MALE,
        originName = "Earth",
        locationName = "Citadel",
        imageUrl = "img",
        episodeUrls = emptyList(),
        createdIso = "2017-11-04T18:48:46.250Z"
    )

    fun mockRick() = default

    fun mockList() = listOf(
        default,
        default.copy(id = 2, name = "Morty")
    )
}