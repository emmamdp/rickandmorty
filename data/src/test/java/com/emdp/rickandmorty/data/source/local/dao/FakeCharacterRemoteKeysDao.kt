package com.emdp.rickandmorty.data.source.local.dao

import com.emdp.rickandmorty.data.source.local.entity.CharacterRemoteKeysEntity

class FakeCharacterRemoteKeysDao : CharacterRemoteKeysDao {
    private val data = mutableMapOf<Int, CharacterRemoteKeysEntity>()

    override suspend fun insertAll(remoteKey: List<CharacterRemoteKeysEntity>) {
        remoteKey.forEach { data[it.characterId] = it }
    }

    override suspend fun remoteKeysByCharacterId(characterId: Int): CharacterRemoteKeysEntity? {
        return data[characterId]
    }

    override suspend fun clearRemoteKeys() {
        data.clear()
    }
}
