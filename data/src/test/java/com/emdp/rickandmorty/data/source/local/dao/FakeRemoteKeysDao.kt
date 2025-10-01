package com.emdp.rickandmorty.data.source.local.dao

import com.emdp.rickandmorty.data.source.local.entity.RemoteKeysEntity
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class FakeRemoteKeysDao : RemoteKeysDao {

    private val mutex = Mutex()
    private val store = hashMapOf<Int, RemoteKeysEntity>()

    override suspend fun upsertAll(keys: List<RemoteKeysEntity>) = mutex.withLock {
        keys.forEach { store[it.characterId] = it }
    }

    override suspend fun remoteKeysById(characterId: Int): RemoteKeysEntity? =
        mutex.withLock { store[characterId] }

    override suspend fun clearRemoteKeys() = mutex.withLock { store.clear() }

    override suspend fun minUpdatedAt(): Long? = mutex.withLock {
        store.values.minOfOrNull { it.updatedAt }
    }
}