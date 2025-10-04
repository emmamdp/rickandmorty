package com.emdp.rickandmorty.data.source.local

import com.emdp.rickandmorty.core.common.result.AppError
import com.emdp.rickandmorty.core.common.result.DataResult
import com.emdp.rickandmorty.data.source.local.dao.CharactersDao
import com.emdp.rickandmorty.data.source.local.entity.CharacterEntityMother
import com.emdp.rickandmorty.data.source.local.mapper.CharacterLocalMapper
import com.emdp.rickandmorty.domain.models.CharacterModelMother
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
internal class CharactersLocalSourceImplTest {

    private val charactersDao: CharactersDao = mock()
    private val localMapper: CharacterLocalMapper = mock()

    private val localSource = CharacterLocalSourceImpl(charactersDao, localMapper)

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
    fun `getCharacterById returns Unknown when dao throws`() = runTest {
        val characterId = 99
        val boom = RuntimeException("db failure")
        whenever(charactersDao.getCharacterById(characterId)).thenThrow(boom)

        val result = localSource.getCharacterById(characterId)

        assertTrue(result is DataResult.Error)
        val error = (result as DataResult.Error).error
        assertInstanceOf(AppError.Unexpected::class.java, error)
        assertEquals("db failure", (error as AppError.Unexpected).cause?.message)
    }
}
