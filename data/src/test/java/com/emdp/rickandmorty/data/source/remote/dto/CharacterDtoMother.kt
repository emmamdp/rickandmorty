package com.emdp.rickandmorty.data.source.remote.dto

internal object CharacterDtoMother {

    private val default = CharacterDto(
        id = 1,
        name = "Rick Sanchez",
        status = "Alive",
        species = "Human",
        type = "",
        gender = "Male",
        origin = LocationRefDtoMother.mockEarth(),
        location = LocationRefDtoMother.mockCitadelOfRicks(),
        image = "https://img/rick.png",
        episode = listOf("e1", "e2"),
        url = "u",
        created = "2017-11-04T18:48:46.250Z"
    )

    fun mockRickSanchez() = default

    fun mockRick() = default.copy(
        name = "Rick",
        location = LocationRefDtoMother.mockCitadelWithoutUrl(),
        image = "img1",
        episode = listOf("e1"),
        url = "u1",
        created = "c1"
    )

    fun mockMorty() = default.copy(
        id = 2,
        name = "Morty",
        status = "Dead",
        gender = "Male",
        location = LocationRefDtoMother.mockCitadelWithoutUrl(),
        image = "img2",
        episode = listOf("e2"),
        url = "u2",
        created = "c2"
    )

    fun mockStatus(status: String) = default.copy(
        id = 99,
        name = "X",
        status = status,
        species = "",
        type = "",
        gender = "Male",
        origin = LocationRefDtoMother.mockEmpty(),
        location = LocationRefDtoMother.mockEmpty(),
        image = "",
        episode = emptyList(),
        url = "",
        created = ""
    )

    fun mockGender(gender: String) = default.copy(
        id = 100,
        name = "Y",
        status = "Alive",
        species = "",
        type = "",
        gender = gender,
        origin = LocationRefDtoMother.mockEmpty(),
        location = LocationRefDtoMother.mockEmpty(),
        image = "",
        episode = emptyList(),
        url = "",
        created = ""
    )
}