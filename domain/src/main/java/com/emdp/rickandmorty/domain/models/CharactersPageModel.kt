package com.emdp.rickandmorty.domain.models

data class CharactersPageModel(
    val count: Int,
    val pages: Int,
    val nextPage: Int?,
    val prevPage: Int?,
    val results: List<CharacterModel>
)