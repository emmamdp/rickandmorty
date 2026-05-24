package com.emdp.rickandmorty.data.source.local.dao

import com.emdp.rickandmorty.data.source.local.entity.CharacterRemoteKeysEntity
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class CharacterRemoteKeysDaoTest {

    private lateinit var dao: CharacterRemoteKeysDao

    @BeforeEach
    fun createDb() {
        dao = FakeCharacterRemoteKeysDao()
    }

    @Test
    fun `insertAll and remoteKeysByCharacterId should work correctly`() = runBlocking {
        val keys = listOf(
            CharacterRemoteKeysEntity(characterId = 1, prevKey = null, nextKey = 2),
            CharacterRemoteKeysEntity(characterId = 2, prevKey = 1, nextKey = 3)
        )

        dao.insertAll(keys)
        val result = dao.remoteKeysByCharacterId(1)

        assertEquals(1, result?.characterId)
        assertEquals(null, result?.prevKey)
        assertEquals(2, result?.nextKey)
    }

    @Test
    fun `clearRemoteKeys should remove all entries`() = runBlocking {
        val keys = listOf(CharacterRemoteKeysEntity(1, null, 2))
        dao.insertAll(keys)

        dao.clearRemoteKeys()
        val result = dao.remoteKeysByCharacterId(1)

        assertNull(result)
    }
}
