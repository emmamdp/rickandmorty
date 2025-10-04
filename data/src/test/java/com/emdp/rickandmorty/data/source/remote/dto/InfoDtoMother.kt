package com.emdp.rickandmorty.data.source.remote.dto

internal object InfoDtoMother {

    private val default = InfoDto(
        count = 826,
        pages = 42,
        next = "https://api/character?page=2",
        prev = "https://api/character?page=1"
    )

    fun mock() = default

    fun mockNextUrl(next: String?) = default.copy(
        next = next
    )

    fun mockPrevUrl(prev: String?) = default.copy(
        prev = prev
    )
}