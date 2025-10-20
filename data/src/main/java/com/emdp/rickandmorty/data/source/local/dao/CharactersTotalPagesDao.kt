package com.emdp.rickandmorty.data.source.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.emdp.rickandmorty.data.source.local.entity.CharactersTotalPagesEntity

@Dao
interface CharactersTotalPagesDao {

    @Query("SELECT totalPages FROM characters_total_pages WHERE filterKey = :filterKey LIMIT 1")
    suspend fun getTotalPages(filterKey: String): Int?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: CharactersTotalPagesEntity)
}