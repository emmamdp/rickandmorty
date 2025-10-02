package com.emdp.rickandmorty.data.source.remote.dto

internal object CharactersResponseDtoMother {

    private val default = CharactersResponseDto(
        info = InfoDtoMother.mock(),
        results = listOf(
            CharacterDtoMother.mockRick(),
            CharacterDtoMother.mockMorty()
        )
    )

    fun mock() = default
}