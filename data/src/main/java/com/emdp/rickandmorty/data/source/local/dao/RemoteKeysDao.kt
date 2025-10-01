package com.emdp.rickandmorty.data.source.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.emdp.rickandmorty.data.source.local.entity.RemoteKeysEntity

@Dao
interface RemoteKeysDao {

    @Upsert
    suspend fun upsertAll(keys: List<RemoteKeysEntity>)

    @Query("SELECT * FROM character_remote_keys WHERE characterId = :characterId LIMIT 1")
    suspend fun remoteKeysById(characterId: Int): RemoteKeysEntity?

    @Query("DELETE FROM character_remote_keys")
    suspend fun clearRemoteKeys()

    @Query("SELECT MIN(updatedAt) FROM character_remote_keys")
    suspend fun minUpdatedAt(): Long?
}