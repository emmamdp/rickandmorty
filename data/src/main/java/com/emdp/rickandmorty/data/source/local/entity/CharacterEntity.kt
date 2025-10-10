package com.emdp.rickandmorty.data.source.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.SkipQueryVerification

@Entity(
    tableName = "characters",
    indices = [
        Index(value = ["name"]),
        Index(value = ["status"]),
        Index(value = ["gender"]),
        Index(value = ["species"]),
        Index(value = ["type"])
    ]
)
@SkipQueryVerification
data class CharacterEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val type: String?,
    val gender: String,
    val imageUrl: String,
    val originName: String?,
    val locationName: String?,
    val episodes: List<String>?,
    val created: String?
)