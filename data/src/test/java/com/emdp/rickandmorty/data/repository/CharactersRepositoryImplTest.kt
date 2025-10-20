package com.emdp.rickandmorty.data.repository

import com.emdp.rickandmorty.core.common.result.AppError
import com.emdp.rickandmorty.core.common.result.DataResult
import com.emdp.rickandmorty.data.source.local.CharacterLocalSource
import com.emdp.rickandmorty.data.source.remote.CharactersRemoteSource
import com.emdp.rickandmorty.domain.models.CharacterModelMother
import com.emdp.rickandmorty.domain.models.CharactersFilterModelMother
import com.emdp.rickandmorty.domain.models.RickAndMortyPagedData
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoInteractions
import org.mockito.kotlin.whenever

internal class CharactersRepositoryImplTest {

    private val localSource: CharacterLocalSource = mock()
    private val remoteSource: CharactersRemoteSource = mock()

    private val repository = CharactersRepositoryImpl(
        localSource = localSource,
        remoteSource = remoteSource
    )

    @Test
    fun `getCharactersPaged returns Success with full local page`() =
        runTest {
            val filter = CharactersFilterModelMother.mock()
            val localPage = List(20) { CharacterModelMother.mockRick() }
            val totalPages = 5

            whenever(localSource.getCharactersPage(page = 1, pageSize = 20, filter = filter))
                .thenReturn(localPage)
            whenever(localSource.getTotalPages(filter = filter))
                .thenReturn(totalPages)

            val result = repository.getCharactersPaged(page = 1, filter = filter)

            assertTrue(result is DataResult.Success)
            val data = (result as DataResult.Success).data
            assertEquals(20, data.data.size)
            assertEquals(1, data.page)
            assertTrue(data.hasMore)
            assertEquals(5, data.totalPages)
            verify(localSource, times(1))
                .getCharactersPage(page = 1, pageSize = 20, filter = filter)
            verifyNoInteractions(remoteSource)
        }

    @Test
    fun `getCharactersPaged fetches from remote when local page is incomplete`() =
        runTest {
            val filter = CharactersFilterModelMother.mock()
            val localPage = List(5) { CharacterModelMother.mockRick() }
            val remoteData = List(20) { CharacterModelMother.mockRick() }
            val remotePagedData = RickAndMortyPagedData(
                data = remoteData,
                page = 1,
                hasMore = true,
                totalPages = 10
            )

            whenever(localSource.getCharactersPage(page = 1, pageSize = 20, filter = filter))
                .thenReturn(localPage)
            whenever(remoteSource.getCharactersPaged(page = 1, filter = filter))
                .thenReturn(DataResult.Success(remotePagedData))

            val result = repository.getCharactersPaged(page = 1, filter = filter)

            assertTrue(result is DataResult.Success)
            val data = (result as DataResult.Success).data
            assertEquals(remoteData, data.data)
            assertTrue(data.hasMore)
            assertEquals(10, data.totalPages)
            verify(localSource).upsertCharacters(remoteData)
            verify(localSource).saveTotalPages(filter = filter, totalPages = 10)
        }

    @Test
    fun `getCharactersPaged returns local data when remote fails but local has data`() = runTest {
        val filter = CharactersFilterModelMother.mock()
        val localPage = List(5) { CharacterModelMother.mockRick() }
        val totalPages = 3

        whenever(localSource.getCharactersPage(page = 1, pageSize = 20, filter = filter))
            .thenReturn(localPage)
        whenever(remoteSource.getCharactersPaged(page = 1, filter = filter))
            .thenReturn(DataResult.Error(error = AppError.Network(Exception("error"))))
        whenever(localSource.getTotalPages(filter = filter)).thenReturn(totalPages)

        val result = repository.getCharactersPaged(1, filter = filter)

        assertTrue(result is DataResult.Success)
        val data = (result as DataResult.Success).data
        assertEquals(localPage, data.data)
        assertTrue(data.hasMore)
    }

    @Test
    fun `getCharactersPaged returns local data without hasMore when on last page`() = runTest {
        val filter = CharactersFilterModelMother.mock()
        val localPage = List(5) { CharacterModelMother.mockRick() }
        val totalPages = 3

        whenever(localSource.getCharactersPage(page = 3, pageSize = 20, filter = filter))
            .thenReturn(localPage)
        whenever(remoteSource.getCharactersPaged(page = 3, filter = filter))
            .thenReturn(DataResult.Error(error = AppError.Network(Exception("error"))))
        whenever(localSource.getTotalPages(filter = filter)).thenReturn(totalPages)

        val result = repository.getCharactersPaged(page = 3, filter = filter)

        assertTrue(result is DataResult.Success)
        val data = (result as DataResult.Success).data
        assertEquals(localPage, data.data)
        assertFalse(data.hasMore)
    }

    @Test
    fun `getCharactersPaged returns Error when remote fails and local is empty`() =
        runTest {
            val filter = CharactersFilterModelMother.mock()
            val error = AppError.Network(Exception("No internet"))

            whenever(localSource.getCharactersPage(page = 1, pageSize = 20, filter = filter))
                .thenReturn(emptyList())
            whenever(remoteSource.getCharactersPaged(page = 1, filter = filter))
                .thenReturn(DataResult.Error(error))

            val result = repository.getCharactersPaged(page = 1, filter = filter)

            assertTrue(result is DataResult.Error)
            assertEquals(error, (result as DataResult.Error).error)
        }

    @Test
    fun `getCharacterById delegates to localSource`() =
        runTest {
            val expectedModel = CharacterModelMother.mockRick()
            val expected = DataResult.Success(expectedModel)

            whenever(localSource.getCharacterById(1)).thenReturn(expected)

            val result = repository.getCharacterById(id = 1)

            assertTrue(result is DataResult.Success)
            assertEquals(expectedModel, (result as DataResult.Success).data)
            verify(localSource, times(1))
                .getCharacterById(id = 1)
        }

    @Test
    fun `getCharacterById returns Error when localSource fails`() =
        runTest {
            val expected =
                DataResult.Error(error = AppError.Unexpected(IllegalStateException("error")))

            whenever(localSource.getCharacterById(id = 42)).thenReturn(expected)

            val result = repository.getCharacterById(id = 42)

            assertTrue(result is DataResult.Error)
            assertEquals(expected.error, (result as DataResult.Error).error)
        }
}