package com.emdp.rickandmorty.data.source.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "character_remote_keys")
data class RemoteKeysEntity(
    @PrimaryKey val characterId: Int,
    val prevKey: Int?,
    val nextKey: Int?,
    val updatedAt: Long
)