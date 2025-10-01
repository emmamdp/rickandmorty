package com.emdp.rickandmorty.data.source.local.entity

internal object RemoteKeysEntityMother {

    private const val NOW = 1_700_000_000_000L
    private const val T1 = 100L
    private const val T2 = 200L

    private val default =
        RemoteKeysEntity(characterId = 1, prevKey = null, nextKey = 2, updatedAt = NOW)

    fun mock01() = default

    fun mock02() = default.copy(characterId = 2, prevKey = 1, nextKey = 3)

    fun mock01T1() = default.copy(updatedAt = T1)

    fun mock02T2() = mock02().copy(updatedAt = T2)
}