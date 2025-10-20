package com.emdp.rickandmorty.data.source.local

import com.emdp.rickandmorty.core.common.result.AppError
import com.emdp.rickandmorty.core.common.result.DataResult
import com.emdp.rickandmorty.data.source.local.dao.CharactersDao
import com.emdp.rickandmorty.data.source.local.dao.CharactersTotalPagesDao
import com.emdp.rickandmorty.data.source.local.entity.CharacterEntityMother
import com.emdp.rickandmorty.data.source.local.entity.CharactersTotalPagesEntity
import com.emdp.rickandmorty.data.source.local.mapper.CharacterLocalMapper
import com.emdp.rickandmorty.domain.models.CharacterModelMother
import com.emdp.rickandmorty.domain.models.CharactersFilterModelMother
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever

internal class CharactersLocalSourceImplTest {

    private val charactersDao: CharactersDao = mock()
    private val totalPagesDao: CharactersTotalPagesDao = mock()
    private val localMapper: CharacterLocalMapper = mock()

    private val localSource = CharacterLocalSourceImpl(
        charactersDao,
        totalPagesDao,
        localMapper
    )

    @Test
    fun `getCharacterById returns Success when entity exists`() = runTest {
        val entity = CharacterEntityMother.mockRick()
        val model = CharacterModelMother.mockRick()
        val characterId = entity.id

        whenever(charactersDao.getCharacterById(characterId)).thenReturn(entity)
        whenever(localMapper.toModel(entity)).thenReturn(model)

        val result = localSource.getCharacterById(characterId)

        assertTrue(result is DataResult.Success)
        assertSame(model, (result as DataResult.Success).data)
    }

    @Test
    fun `getCharacterById returns DataNotFound when entity is null`() = runTest {
        val characterId = 42
        whenever(charactersDao.getCharacterById(characterId)).thenReturn(null)

        val result = localSource.getCharacterById(characterId)

        assertTrue(result is DataResult.Error)
        val error = (result as DataResult.Error).error
        assertEquals(AppError.DataNotFound, error)
    }

    @Test
    fun `getCharacterById returns Unexpected when dao throws`() = runTest {
        val characterId = 99
        val boom = RuntimeException("db failure")
        whenever(charactersDao.getCharacterById(characterId)).thenThrow(boom)

        val result = localSource.getCharacterById(characterId)

        assertTrue(result is DataResult.Error)
        val error = (result as DataResult.Error).error
        assertInstanceOf(AppError.Unexpected::class.java, error)
        assertEquals("db failure", (error as AppError.Unexpected).cause?.message)
    }

    @Test
    fun `getCharactersPage returns mapped models`() = runTest {
        val filter = CharactersFilterModelMother.mock()
        val entities = CharacterEntityMother.mockList01()
        val models = CharacterModelMother.mockList()

        whenever(
            charactersDao.getCharacters(
                name = filter.name,
                status = filter.status,
                species = filter.species,
                type = filter.type,
                gender = filter.gender,
                limit = 20,
                offset = 0
            )
        ).thenReturn(entities)
        whenever(localMapper.toModel(entities[0])).thenReturn(models[0])
        whenever(localMapper.toModel(entities[1])).thenReturn(models[1])

        val result = localSource.getCharactersPage(page = 1, pageSize = 20, filter = filter)

        assertEquals(models, result)
    }

    @Test
    fun `getCharactersPage returns empty list when dao throws`() = runTest {
        val filter = CharactersFilterModelMother.mock()
        whenever(
            charactersDao.getCharacters(
                name = any(),
                status = any(),
                species = any(),
                type = any(),
                gender = any(),
                limit = any(),
                offset = any()
            )
        ).thenThrow(RuntimeException("error"))

        val result = localSource.getCharactersPage(page = 1, pageSize = 20, filter = filter)

        assertTrue(result.isEmpty())
    }

    @Test
    fun `getCharactersPage calculates correct offset for page 2`() = runTest {
        whenever(
            charactersDao.getCharacters(
                name = null,
                status = null,
                species = null,
                type = null,
                gender = null,
                limit = 20,
                offset = 20
            )
        ).thenReturn(emptyList())

        localSource.getCharactersPage(page = 2, pageSize = 20, filter = null)

        verify(charactersDao).getCharacters(
            name = null,
            status = null,
            species = null,
            type = null,
            gender = null,
            limit = 20,
            offset = 20
        )
    }

    @Test
    fun `upsertCharacters maps models to entities and saves them`() = runTest {
        val models = listOf(CharacterModelMother.mockRick())
        val entities = listOf(CharacterEntityMother.mockRick())

        whenever(localMapper.toEntity(models[0])).thenReturn(entities[0])

        localSource.upsertCharacters(models)

        verify(charactersDao).upsertAll(characters = entities)
    }

    @Test
    fun `upsertCharacters does nothing when list is empty`() = runTest {
        localSource.upsertCharacters(emptyList())

        verifyNoInteractions(charactersDao)
    }

    @Test
    fun `getTotalPages returns value from dao`() = runTest {
        val filter = CharactersFilterModelMother.mock()

        whenever(totalPagesDao.getTotalPages(any())).thenReturn(5)

        val result = localSource.getTotalPages(filter)

        assertEquals(5, result)
    }

    @Test
    fun `saveTotalPages saves entity with correct filter key`() = runTest {
        val filter = CharactersFilterModelMother.mockRickNull()

        localSource.saveTotalPages(filter, 10)

        argumentCaptor<CharactersTotalPagesEntity>().apply {
            verify(totalPagesDao).upsert(capture())
            val entity = firstValue
            assertTrue(entity.filterKey.contains("name=rick"))
            assertEquals(10, entity.totalPages)
        }
    }
}