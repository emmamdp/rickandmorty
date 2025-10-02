package com.emdp.rickandmorty.data.source.remote.dto

internal object LocationRefDtoMother {

    private val default = LocationRefDto(name = "Earth", url = "u1")

    fun mockEarth() = default

    fun mockCitadelOfRicks() = default.copy(name = "Citadel of Ricks", url = "u2")

    fun mockCitadelWithoutUrl() = default.copy(name = "Citadel", url = "")

    fun mockEmpty() = default.copy(name = "", url = "")
}