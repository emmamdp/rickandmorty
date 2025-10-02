package com.emdp.rickandmorty.data.repository

import com.emdp.rickandmorty.core.common.result.AppError
import com.emdp.rickandmorty.core.common.result.DataResult
import com.emdp.rickandmorty.data.source.remote.CharactersRemoteSource
import com.emdp.rickandmorty.domain.models.CharacterModelMother
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.kotlin.whenever
import java.io.IOException

internal class CharactersRepositoryImplTest {

    private val remoteSource: CharactersRemoteSource = Mockito.mock(CharactersRemoteSource::class.java)
    private val repository = CharactersRepositoryImpl(remoteSource)

    @Test
    fun `getCharacters returns Success and delegates to remote with filters`() = runTest {
        val page = 1
        val name = "rick"
        val status = "alive"
        val species = "human"
        val type: String? = null
        val gender = "male"
        val expectedList = CharacterModelMother.mockList()
        val expected = DataResult.Success(expectedList)

        whenever(
            remoteSource.getCharacters(page, name, status, species, type, gender)
        ).thenReturn(expected)

        val result = repository.getCharacters(page, name, status, species, type, gender)

        assertTrue(result is DataResult.Success)
        assertEquals(expectedList, (result as DataResult.Success).data)
        verify(remoteSource, times(1))
            .getCharacters(page, name, status, species, type, gender)
    }

    @Test
    fun `getCharacters returns Error when remote fails`() = runTest {
        val expected = DataResult.Error(error = AppError.Network(IOException("timeout")))

        whenever(
            remoteSource.getCharacters(page = null, name = null, status = null, species = null, type = null, gender = null)
        ).thenReturn(expected)

        val result = repository.getCharacters(
            page = null, name = null, status = null, species = null, type = null, gender = null
        )

        assertTrue(result is DataResult.Error)
        assertEquals(expected.error, (result as DataResult.Error).error)
        verify(remoteSource, times(1))
            .getCharacters(page = null, name = null, status = null, species = null, type = null, gender = null)
    }

    @Test
    fun `getCharacterById returns Success and delegates to remote`() = runTest {
        val expectedModel = CharacterModelMother.mockRick()
        val expected = DataResult.Success(expectedModel)

        whenever(remoteSource.getCharacterById(1)).thenReturn(expected)

        val result = repository.getCharacterById(1)

        assertTrue(result is DataResult.Success)
        assertEquals(expectedModel, (result as DataResult.Success).data)
        verify(remoteSource, times(1)).getCharacterById(1)
    }

    @Test
    fun `getCharacterById returns Error when remote fails`() = runTest {
        val expected = DataResult.Error(AppError.Unexpected(IllegalStateException("boom")))
        whenever(remoteSource.getCharacterById(42)).thenReturn(expected)

        val result = repository.getCharacterById(42)

        assertTrue(result is DataResult.Error)
        assertEquals(expected.error, (result as DataResult.Error).error)
        verify(remoteSource, times(1)).getCharacterById(42)
    }
}