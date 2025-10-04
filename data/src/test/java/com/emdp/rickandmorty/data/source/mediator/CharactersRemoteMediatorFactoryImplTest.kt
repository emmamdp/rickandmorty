package com.emdp.rickandmorty.data.source.mediator

import com.emdp.rickandmorty.data.source.local.RickAndMortyDatabase
import com.emdp.rickandmorty.data.source.local.dao.CharactersDao
import com.emdp.rickandmorty.data.source.local.dao.RemoteKeysDao
import com.emdp.rickandmorty.data.source.local.entity.CharacterEntity
import com.emdp.rickandmorty.data.source.local.entity.CharacterEntityMother
import com.emdp.rickandmorty.data.source.local.mapper.CharacterLocalMapper
import com.emdp.rickandmorty.data.source.remote.CharactersRemoteSource
import com.emdp.rickandmorty.domain.models.CharacterModel
import com.emdp.rickandmorty.domain.models.CharacterModelMother
import com.emdp.rickandmorty.domain.models.CharactersFilterModel
import com.emdp.rickandmorty.domain.models.CharactersFilterModelMother
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.Mockito.verifyNoInteractions
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever

internal class CharactersRemoteMediatorFactoryImplTest {

    private val database: RickAndMortyDatabase = mock()
    private val charactersDao: CharactersDao = mock()
    private val remoteKeysDao: RemoteKeysDao = mock()
    private val remoteSource: CharactersRemoteSource = mock()
    private val mapper: CharacterLocalMapper = mock()

    private val factory = CharactersRemoteMediatorFactoryImpl(
        database = database,
        charactersDao = charactersDao,
        remoteKeysDao = remoteKeysDao,
        remoteSource = remoteSource,
        mapper = mapper
    )

    @Test
    fun `create wires dependencies, passes filter and mapper function`() = runTest {
        val filter = CharactersFilterModelMother.mock()

        val mediator = factory.create(filter)

        assertTrue(mediator is CharactersRemoteMediator)

        fun <T> field(obj: Any, name: String): T {
            val f = obj.javaClass.getDeclaredField(name).apply { isAccessible = true }
            @Suppress("UNCHECKED_CAST")
            return f.get(obj) as T
        }

        assertSame(database, field<RickAndMortyDatabase>(mediator, "database"))
        assertSame(charactersDao, field<CharactersDao>(mediator, "charactersDao"))
        assertSame(remoteKeysDao, field<RemoteKeysDao>(mediator, "remoteKeysDao"))
        assertSame(remoteSource, field<CharactersRemoteSource>(mediator, "remoteSource"))
        assertEquals(filter, field<CharactersFilterModel?>(mediator, "filter"))
        verifyNoInteractions(mapper)

        val toEntity: (List<CharacterModel>) -> List<CharacterEntity> =
            field(mediator, "toEntity")

        val domain = listOf(CharacterModelMother.mockRick())
        val mapped = listOf(CharacterEntityMother.mockRick())
        whenever(mapper.toEntityList(domain)).thenReturn(mapped)

        val result = toEntity(domain)

        assertSame(mapped, result)
        verify(mapper, times(1)).toEntityList(domain)
        verifyNoMoreInteractions(mapper)
    }

    @Test
    fun `create allows null filter`() {
        val mediator = factory.create(null)
        assertTrue(mediator is CharactersRemoteMediator)

        fun <T> field(obj: Any, name: String): T {
            val f = obj.javaClass.getDeclaredField(name).apply { isAccessible = true }
            @Suppress("UNCHECKED_CAST")
            return f.get(obj) as T
        }

        assertNull(field<CharactersFilterModel?>(mediator, "filter"))
    }
}