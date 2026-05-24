package com.emdp.rickandmorty.data.source.local.entity

internal object CharacterEntityMother {

    private val character = CharacterEntity(
        id = 1,
        name = "Rick Sanchez",
        status = "Alive",
        species = "Human",
        type = null,
        gender = "Male",
        imageUrl = "https://example.com/image.png",
        originName = null,
        locationName = null,
        episodes = null,
        created = "2017-11-04T18:48:46.250Z"
    )
    private val characterMortySmith = character.copy(id = 2, name = "Morty Smith")

    fun mockRickySanchez() = character

    fun mockRick() = character.copy(name = "Rick")

    fun mockBirdperson() = character.copy(
        id = 3,
        name = "Birdperson",
        status = "DEAD",
        species = "Bird-Person",
        type = null,
        gender = "MALE",
        imageUrl = "https://img/birdperson.png",
        originName = null,
        locationName = null,
        episodes = null,
        created = null
    )

    fun mockUnknown() = character.copy(
        id = 4,
        name = "Unknown Dude",
        status = "SOMETHING_WEIRD",
        species = "???",
        type = "???",
        gender = "NOT_A_GENDER",
        imageUrl = "https://img/unknown.png",
        originName = "Somewhere",
        locationName = "Nowhere",
        episodes = listOf("e1"),
        created = "2020-01-01T00:00:00Z"
    )

    fun mockCaseSensitive() = character.copy(
        id = 5,
        name = "Case Test",
        status = "alive",
        species = "Human",
        type = "",
        gender = "male",
        imageUrl = "https://img/case.png",
        originName = "earth",
        locationName = "somewhere",
        episodes = listOf("e1", "e2"),
        created = "date"
    )

    fun mockList01() = listOf(
        mockRick(),
        character.copy(id = 2, name = "Morty")
    )

    fun mockList02() = listOf(
        character,
        characterMortySmith,
        character.copy(id = 3, name = "Birdperson", status = "Dead", species = "Alien"),
        character.copy(id = 4, name = "Summer Smith", gender = "Female"),
        character.copy(id = 5, name = "Abradolf Lincler", status = "unknown", type = "Clone")
    )
}
