package com.emdp.rickandmorty.domain.models

data class CharactersFilterModel(
    val name: String? = null,
    val status: String? = null,
    val species: String? = null,
    val type: String? = null,
    val gender: String? = null
)