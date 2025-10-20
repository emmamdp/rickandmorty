package com.emdp.rickandmorty.domain.models

data class RickAndMortyPagedData<T>(
    val data: List<T>,
    val page: Int,
    val hasMore: Boolean,
    val totalPages: Int? = null
)