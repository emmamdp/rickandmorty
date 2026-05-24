package com.emdp.rickandmorty.data.source.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.emdp.rickandmorty.data.source.local.entity.CharacterRemoteKeysEntity

@Dao
interface CharacterRemoteKeysDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<CharacterRemoteKeysEntity>)

    @Query("SELECT * FROM character_remote_keys WHERE characterId = :characterId")
    suspend fun remoteKeysByCharacterId(characterId: Int): CharacterRemoteKeysEntity?

    @Query("DELETE FROM character_remote_keys")
    suspend fun clearRemoteKeys()
}
