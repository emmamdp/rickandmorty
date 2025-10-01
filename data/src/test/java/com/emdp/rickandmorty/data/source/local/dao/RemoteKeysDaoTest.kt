package com.emdp.rickandmorty.data.source.local.dao

import com.emdp.rickandmorty.data.source.local.entity.RemoteKeysEntityMother
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class RemoteKeysDaoTest {

    private lateinit var dao: RemoteKeysDao

    @BeforeEach
    fun setUp() {
        dao = FakeRemoteKeysDao()
    }

    @Test
    fun upsertAndQuery_shouldReturnInsertedKeys() = runBlocking {
        val keys = listOf(
            RemoteKeysEntityMother.mock01(),
            RemoteKeysEntityMother.mock02()
        )

        dao.upsertAll(keys)

        val k1 = dao.remoteKeysById(1)
        val k2 = dao.remoteKeysById(2)

        assertEquals(null, k1?.prevKey)
        assertEquals(2, k1?.nextKey)
        assertEquals(1, k2?.prevKey)
        assertEquals(3, k2?.nextKey)
    }

    @Test
    fun clearRemoteKeys_shouldDeleteAll() = runBlocking {
        dao.upsertAll(listOf(RemoteKeysEntityMother.mock01()))
        dao.clearRemoteKeys()
        val k1 = dao.remoteKeysById(1)

        assertNull(k1)
    }

    @Test
    fun minUpdatedAt_shouldReturnMinValue() = runBlocking {
        val t1 = 100L

        dao.upsertAll(
            listOf(
                RemoteKeysEntityMother.mock01T1(),
                RemoteKeysEntityMother.mock02T2()
            )
        )

        val min = dao.minUpdatedAt()
        assertEquals(t1, min)
    }
}