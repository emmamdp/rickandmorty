package com.emdp.rickandmorty.data.source.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "characters_total_pages")
data class CharactersTotalPagesEntity(
    @PrimaryKey val filterKey: String,
    val totalPages: Int,
    val updatedAtMillis: Long
)