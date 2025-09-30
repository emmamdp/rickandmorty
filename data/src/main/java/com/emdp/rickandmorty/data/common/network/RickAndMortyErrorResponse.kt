package com.emdp.rickandmorty.data.common.network

data class RickAndMortyErrorResponse(
    val error: String? = null,
    val message: String? = null,
    val status: Int? = null
)
