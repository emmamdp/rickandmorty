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

    fun mockInfoNextUrl(next: String?) = default.copy(
        info = InfoDtoMother.mockNextUrl(next)
    )

    fun mockInfoPrevUrl(prev: String?) = default.copy(
        info = InfoDtoMother.mockPrevUrl(prev)
    )
}