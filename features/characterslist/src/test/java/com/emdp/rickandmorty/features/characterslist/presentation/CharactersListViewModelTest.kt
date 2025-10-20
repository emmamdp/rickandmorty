package com.emdp.rickandmorty.features.characterslist.presentation

import com.emdp.rickandmorty.core.common.result.AppError
import com.emdp.rickandmorty.core.common.result.DataResult
import com.emdp.rickandmorty.domain.models.CharacterModel
import com.emdp.rickandmorty.domain.models.RickAndMortyPagedData
import com.emdp.rickandmorty.domain.usecase.characterslist.GetCharactersUseCase
import com.emdp.rickandmorty.features.characterslist.domain.models.CharactersFilterModelMother
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.kotlin.*

@OptIn(ExperimentalCoroutinesApi::class)
internal class CharactersListViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: CharactersListViewModel
    private val getCharactersUseCase: GetCharactersUseCase = mock()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = CharactersListViewModel(getCharactersUseCase = getCharactersUseCase)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial filterState is null`() {
        assertNull(viewModel.filterState.value)
    }

    @Test
    fun `initial state has empty characters list`() {
        val initialState = viewModel.state.value

        assertTrue(initialState.characters.isEmpty())
        assertFalse(initialState.isLoading)
        assertFalse(initialState.isLoadingMore)
        assertTrue(initialState.hasMore)
        assertEquals(1, initialState.currentPage)
        assertNull(initialState.error)
    }

    @Test
    fun `loadCharacters sets loading state and loads first page`() = runTest {
        val pagedData = RickAndMortyPagedData(
            data = listOf(mock<CharacterModel>()),
            page = 1,
            hasMore = true,
            totalPages = 5
        )

        whenever(getCharactersUseCase(1, null))
            .thenReturn(DataResult.Success(pagedData))

        viewModel.loadCharacters(refresh = true)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(1, state.characters.size)
        assertFalse(state.isLoading)
        assertTrue(state.hasMore)
        assertEquals(2, state.currentPage)
        assertNull(state.error)
        verify(getCharactersUseCase, times(1))
            .invoke(1, null)
    }

    @Test
    fun `loadCharacters with error updates error state`() = runTest {
        val error = AppError.Network(Exception("No internet"))

        whenever(getCharactersUseCase(1, null))
            .thenReturn(DataResult.Error(error))

        viewModel.loadCharacters(refresh = true)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state.characters.isEmpty())
        assertFalse(state.isLoading)
        assertEquals(error, state.error)
    }

    @Test
    fun `loadMore appends characters to existing list`() = runTest {
        val page1 = RickAndMortyPagedData(
            data = listOf(mock<CharacterModel>()),
            page = 1,
            hasMore = true,
            totalPages = 2
        )
        val page2 = RickAndMortyPagedData(
            data = listOf(mock<CharacterModel>()),
            page = 2,
            hasMore = false,
            totalPages = 2
        )

        whenever(getCharactersUseCase(1, null))
            .thenReturn(DataResult.Success(page1))
        whenever(getCharactersUseCase(2, null))
            .thenReturn(DataResult.Success(page2))

        viewModel.loadCharacters(refresh = true)
        advanceUntilIdle()

        viewModel.loadMore()
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(2, state.characters.size)
        assertFalse(state.hasMore)
        assertEquals(3, state.currentPage)
    }

    @Test
    fun `loadMore does nothing when already loading`() = runTest {
        val pagedData = RickAndMortyPagedData(
            data = listOf(mock<CharacterModel>()),
            page = 1,
            hasMore = true,
            totalPages = 5
        )

        whenever(getCharactersUseCase(any(), anyOrNull()))
            .thenReturn(DataResult.Success(pagedData))

        viewModel.loadCharacters(refresh = true)
        viewModel.loadMore()
        advanceUntilIdle()

        verify(getCharactersUseCase, times(2))
            .invoke(any(), anyOrNull())
    }

    @Test
    fun `loadMore does nothing when no more pages`() = runTest {
        val pagedData = RickAndMortyPagedData(
            data = listOf(mock<CharacterModel>()),
            page = 1,
            hasMore = false,
            totalPages = 1
        )

        whenever(getCharactersUseCase(1, null))
            .thenReturn(DataResult.Success(pagedData))

        viewModel.loadCharacters(refresh = true)
        advanceUntilIdle()

        viewModel.loadMore()
        advanceUntilIdle()

        verify(getCharactersUseCase, times(1))
            .invoke(any(), anyOrNull())
    }

    @Test
    fun `refresh reloads from page 1`() = runTest {
        val page1 = RickAndMortyPagedData(
            data = listOf(mock<CharacterModel>()),
            page = 1,
            hasMore = true,
            totalPages = 2
        )
        val page2 = RickAndMortyPagedData(
            data = listOf(mock<CharacterModel>()),
            page = 2,
            hasMore = false,
            totalPages = 2
        )

        whenever(getCharactersUseCase(any(), anyOrNull()))
            .thenReturn(DataResult.Success(page1))
            .thenReturn(DataResult.Success(page2))
            .thenReturn(DataResult.Success(page1))

        viewModel.loadCharacters(refresh = true)
        advanceUntilIdle()

        viewModel.loadMore()
        advanceUntilIdle()

        viewModel.refresh()
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(1, state.characters.size)
        assertEquals(2, state.currentPage)
    }

    @Test
    fun `applyFilter updates filterState and reloads`() = runTest {
        val filter = CharactersFilterModelMother.mock()
        val pagedData = RickAndMortyPagedData(
            data = listOf(mock<CharacterModel>()),
            page = 1,
            hasMore = false,
            totalPages = 1
        )

        whenever(getCharactersUseCase(1, filter))
            .thenReturn(DataResult.Success(pagedData))

        viewModel.applyFilter(filter)
        advanceUntilIdle()

        assertEquals(filter, viewModel.filterState.value)
        verify(getCharactersUseCase, times(1))
            .invoke(1, filter)
    }

    @Test
    fun `clearFilter resets filterState to null and reloads`() = runTest {
        val filter = CharactersFilterModelMother.mock()
        val pagedData = RickAndMortyPagedData(
            data = listOf(mock<CharacterModel>()),
            page = 1,
            hasMore = false,
            totalPages = 1
        )

        whenever(getCharactersUseCase(any(), anyOrNull()))
            .thenReturn(DataResult.Success(pagedData))

        viewModel.applyFilter(filter)
        advanceUntilIdle()

        viewModel.clearFilter()
        advanceUntilIdle()

        assertNull(viewModel.filterState.value)
        verify(getCharactersUseCase, times(1))
            .invoke(1, null)
    }
}