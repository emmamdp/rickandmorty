package com.emdp.rickandmorty.data.repository

import androidx.paging.PagingData
import com.emdp.rickandmorty.core.common.result.AppError
import com.emdp.rickandmorty.core.common.result.DataResult
import com.emdp.rickandmorty.data.source.local.CharacterLocalSource
import com.emdp.rickandmorty.data.source.local.dao.CharactersDao
import com.emdp.rickandmorty.data.source.local.entity.CharacterEntity
import com.emdp.rickandmorty.data.source.remote.CharactersRemoteSource
import com.emdp.rickandmorty.domain.models.CharacterModel
import com.emdp.rickandmorty.domain.models.CharacterModelMother
import com.emdp.rickandmorty.domain.models.CharactersFilterModelMother
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoInteractions
import org.mockito.kotlin.whenever

internal class CharactersRepositoryImplTest {

    private val localSource: CharacterLocalSource = mock()
    private val charactersDao: CharactersDao = mock()
    private val remoteSource: CharactersRemoteSource = mock()
    private val toEntity: (List<CharacterModel>) -> List<CharacterEntity> = mock()

    private val repository = CharactersRepositoryImpl(
        localSource = localSource,
        charactersDao = charactersDao,
        remoteSource = remoteSource,
        toEntity = toEntity
    )

    @Test
    fun `getCharactersPaged returns flow with PagingData`() = runTest {
        val filter = CharactersFilterModelMother.mock()

        val flow = repository.getCharactersPaged(filter)

        assertNotNull(flow)
        assertTrue(flow is kotlinx.coroutines.flow.Flow<PagingData<CharacterModel>>)
        verifyNoInteractions(localSource)
        verifyNoInteractions(charactersDao)
        verifyNoInteractions(remoteSource)
    }

    @Test
    fun `getCharactersPaged with null filter returns flow`() = runTest {
        val flow = repository.getCharactersPaged(filter = null)

        assertNotNull(flow)
        assertTrue(flow is kotlinx.coroutines.flow.Flow<PagingData<CharacterModel>>)
        verifyNoInteractions(localSource)
        verifyNoInteractions(charactersDao)
        verifyNoInteractions(remoteSource)
    }

    @Test
    fun `getCharacterById returns Success and delegates to localSource`() = runTest {
        val expectedModel = CharacterModelMother.mockRick()
        val expected = DataResult.Success(expectedModel)

        whenever(localSource.getCharacterById(1)).thenReturn(expected)

        val result = repository.getCharacterById(1)

        assertTrue(result is DataResult.Success)
        assertEquals(expectedModel, (result as DataResult.Success).data)
        verify(localSource, times(1)).getCharacterById(1)
        verifyNoInteractions(charactersDao)
        verifyNoInteractions(remoteSource)
    }

    @Test
    fun `getCharacterById returns Error when localSource fails`() = runTest {
        val expected = DataResult.Error(AppError.Unexpected(IllegalStateException("boom")))

        whenever(localSource.getCharacterById(42)).thenReturn(expected)

        val result = repository.getCharacterById(42)

        assertTrue(result is DataResult.Error)
        assertEquals(expected.error, (result as DataResult.Error).error)
        verify(localSource, times(1)).getCharacterById(42)
        verifyNoInteractions(charactersDao)
        verifyNoInteractions(remoteSource)
    }

    @Test
    fun `getCharacterById returns DataNotFound error when character does not exist`() = runTest {
        val expected = DataResult.Error(AppError.DataNotFound)

        whenever(localSource.getCharacterById(999)).thenReturn(expected)

        val result = repository.getCharacterById(999)

        assertTrue(result is DataResult.Error)
        assertEquals(AppError.DataNotFound, (result as DataResult.Error).error)
        verify(localSource, times(1)).getCharacterById(999)
    }
}