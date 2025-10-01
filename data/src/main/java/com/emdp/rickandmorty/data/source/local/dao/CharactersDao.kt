package com.emdp.rickandmorty.data.source.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.emdp.rickandmorty.data.source.local.entity.CharacterEntity

@Dao
interface CharactersDao {

    @Query(
        """
        SELECT * FROM characters
        WHERE (:name IS NULL OR name LIKE '%' || :name || '%' ESCAPE '\' )
          AND (:status IS NULL OR status = :status)
          AND (:gender IS NULL OR gender = :gender)
          AND (:species IS NULL OR species = :species)
          AND (:type IS NULL OR type = :type)
        ORDER BY name COLLATE NOCASE ASC, id ASC
        """
    )
    fun pagingSource(
        name: String? = null,
        status: String? = null,
        gender: String? = null,
        species: String? = null,
        type: String? = null
    ): PagingSource<Int, CharacterEntity>

    @Query("SELECT * FROM characters WHERE id = :id LIMIT 1")
    suspend fun getCharacterById(id: Int): CharacterEntity?

    @Upsert
    suspend fun upsertAll(characters: List<CharacterEntity>)

    @Query("DELETE FROM characters")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM characters")
    suspend fun count(): Int
}