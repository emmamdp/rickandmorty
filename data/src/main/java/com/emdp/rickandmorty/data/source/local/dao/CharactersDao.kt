package com.emdp.rickandmorty.data.source.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.emdp.rickandmorty.data.source.local.entity.CharacterEntity

@Dao
interface CharactersDao {

    @Query(
        """
            SELECT * FROM characters
            WHERE (:name IS NULL OR name LIKE '%' || :name || '%')
              AND (:status IS NULL OR status = :status)
              AND (:species IS NULL OR species = :species)
              AND (:type IS NULL OR type = :type)
              AND (:gender IS NULL OR gender = :gender)
            ORDER BY orderIndex ASC
            """
    )
    fun pagingSource(
        name: String?,
        status: String?,
        species: String?,
        type: String?,
        gender: String?
    ): PagingSource<Int, CharacterEntity>

    @Query("SELECT * FROM characters WHERE id = :id LIMIT 1")
    suspend fun getCharacterById(id: Int): CharacterEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)  // ← NUEVO
    suspend fun insertAll(characters: List<CharacterEntity>)

    @Upsert
    suspend fun upsertAll(characters: List<CharacterEntity>)

    @Query("DELETE FROM characters")
    suspend fun clearAll()

    @Query("SELECT * FROM characters ORDER BY orderIndex ASC LIMIT 1")
    suspend fun firstItem(): CharacterEntity?

    @Query("SELECT * FROM characters ORDER BY orderIndex DESC LIMIT 1")
    suspend fun lastItem(): CharacterEntity?

    @Query("SELECT MAX(orderIndex) FROM characters")
    suspend fun maxOrderIndex(): Long?
}