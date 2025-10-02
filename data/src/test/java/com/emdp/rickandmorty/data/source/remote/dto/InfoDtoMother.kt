package com.emdp.rickandmorty.data.source.remote.dto

internal object InfoDtoMother {

    private val default = InfoDto(count = 2, pages = 1, next = null, prev = null)

    fun mock() = default
}