package com.emdp.rickandmorty.data.source.remote.dto

internal object InfoDtoMother {

    private val default = InfoDto(count = 826, pages = 42, next = "3", prev = "1")

    fun mock() = default
}