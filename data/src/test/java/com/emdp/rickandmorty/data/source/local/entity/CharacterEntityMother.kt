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
        created = "2017-11-04T18:48:46.250Z"
    )
    private val characterMortySmith = character.copy(id = 2, name = "Morty Smith")

    fun mockRickySanchez() = character

    fun mockRick() = character.copy(name = "Rick")

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

    fun mockRandomListEntities(min: Int, max: Int) = (min..max).map { id ->
        character.copy(
            id = id,
            name = "Char $id",
            imageUrl = "https://img/$id.png",
            originName = "Earth",
            locationName = "Citadel"
        )
    }
}