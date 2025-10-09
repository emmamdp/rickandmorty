package com.emdp.rickandmorty.features.advancedsearch.presentation

import com.emdp.rickandmorty.core.common.result.AppError
import com.emdp.rickandmorty.core.common.result.DataResult
import com.emdp.rickandmorty.domain.models.CharactersPageModel
import com.emdp.rickandmorty.domain.usecase.advancedsearch.AdvancedSearchUseCase
import com.emdp.rickandmorty.features.advancedsearch.models.CharacterModelMother
import com.emdp.rickandmorty.features.advancedsearch.models.CharactersPageModelMother
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoInteractions
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
internal class RickAndMortyAdvancedSearchViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val useCase: AdvancedSearchUseCase = mock()
    private lateinit var viewModel: RickAndMortyAdvancedSearchViewModel

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = RickAndMortyAdvancedSearchViewModel(useCase)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Idle`() {
        assertTrue(viewModel.uiState.value is AdvancedSearchUiState.Idle)
    }

    @Test
    fun `initial filters are empty`() {
        val filters = viewModel.filters.value

        assertEquals(null, filters.name)
        assertEquals(null, filters.status)
        assertEquals(null, filters.species)
        assertEquals(null, filters.gender)
        assertEquals(null, filters.type)
    }

    @Test
    fun `updateName updates filter name`() {
        viewModel.updateName("Rick")

        assertEquals("Rick", viewModel.filters.value.name)
    }

    @Test
    fun `updateName with blank resets to Idle when no other filters`() {
        viewModel.updateName("Rick")
        viewModel.updateName("")

        assertEquals(null, viewModel.filters.value.name)
        assertTrue(viewModel.uiState.value is AdvancedSearchUiState.Idle)
    }

    @Test
    fun `updateStatus updates filter and triggers search`() = runTest {
        val page = CharactersPageModelMother.mock()

        whenever(useCase.invoke(page = eq(1), filters = any()))
            .thenReturn(DataResult.Success(page))

        viewModel.updateStatus("alive")
        advanceUntilIdle()

        assertEquals("alive", viewModel.filters.value.status)
        assertTrue(viewModel.uiState.value is AdvancedSearchUiState.Success)
        verify(useCase, times(1))
            .invoke(page = eq(1), filters = any())
    }

    @Test
    fun `updateSpecies updates filter and triggers search`() = runTest {
        val page = CharactersPageModelMother.mock()

        whenever(useCase.invoke(page = eq(1), filters = any()))
            .thenReturn(DataResult.Success(page))

        viewModel.updateSpecies("Human")
        advanceUntilIdle()

        assertEquals("Human", viewModel.filters.value.species)
        assertTrue(viewModel.uiState.value is AdvancedSearchUiState.Success)
        verify(useCase, times(1))
            .invoke(page = eq(1), filters = any())
    }

    @Test
    fun `updateGender updates filter and triggers search`() = runTest {
        val page = CharactersPageModelMother.mock()

        whenever(useCase.invoke(page = eq(1), filters = any()))
            .thenReturn(DataResult.Success(page))

        viewModel.updateGender("male")
        advanceUntilIdle()

        assertEquals("male", viewModel.filters.value.gender)
        assertTrue(viewModel.uiState.value is AdvancedSearchUiState.Success)
        verify(useCase, times(1))
            .invoke(page = eq(1), filters = any())
    }

    @Test
    fun `updateType updates filter but does not trigger search`() = runTest {
        viewModel.updateType("Genetic experiment")
        advanceUntilIdle()

        assertEquals("Genetic experiment", viewModel.filters.value.type)
        assertTrue(viewModel.uiState.value is AdvancedSearchUiState.Idle)
        verifyNoInteractions(useCase)
    }

    @Test
    fun `clearFilters resets all filters and state to Idle`() {
        viewModel.updateName("Rick")
        viewModel.updateType("Clone")

        viewModel.clearFilters()

        val filters = viewModel.filters.value
        assertEquals(null, filters.name)
        assertEquals(null, filters.status)
        assertEquals(null, filters.species)
        assertEquals(null, filters.gender)
        assertEquals(null, filters.type)
        assertTrue(viewModel.uiState.value is AdvancedSearchUiState.Idle)
    }

    @Test
    fun `search with no filters resets to Idle`() = runTest {
        viewModel.search()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value is AdvancedSearchUiState.Idle)
        verifyNoInteractions(useCase)
    }

    @Test
    fun `search with filters returns Success with characters`() = runTest {
        val page = CharactersPageModelMother.mock()

        whenever(useCase.invoke(page = eq(1), filters = any()))
            .thenReturn(DataResult.Success(page))

        viewModel.updateName("Rick")
        viewModel.search()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is AdvancedSearchUiState.Success)
        assertEquals(page.results.size, (state as AdvancedSearchUiState.Success).characters.size)
        assertTrue(state.hasMorePages)
    }

    @Test
    fun `search with no next page returns Success with hasMorePages false`() = runTest {
        val page = CharactersPageModelMother.mock().copy(nextPage = null)

        whenever(useCase.invoke(page = eq(1), filters = any()))
            .thenReturn(DataResult.Success(page))

        viewModel.updateName("Rick")
        viewModel.search()
        advanceUntilIdle()

        val state = viewModel.uiState.value as AdvancedSearchUiState.Success
        assertEquals(false, state.hasMorePages)
    }

    @Test
    fun `search with network error returns Error state`() = runTest {
        val error = DataResult.Error(AppError.Network(cause = Exception("No internet")))

        whenever(useCase.invoke(page = eq(1), filters = any()))
            .thenReturn(error)

        viewModel.updateName("Rick")
        viewModel.search()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value is AdvancedSearchUiState.Error)
    }

    @Test
    fun `search with 404 error returns Error state`() = runTest {
        val error = DataResult.Error(AppError.Http(code = 404, message = "Not found"))

        whenever(useCase.invoke(page = eq(1), filters = any()))
            .thenReturn(error)

        viewModel.updateName("Rick")
        viewModel.search()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value is AdvancedSearchUiState.Error)
    }

    @Test
    fun `loadMore appends characters to existing list`() = runTest {
        val page1 = CharactersPageModel(
            count = 2,
            pages = 2,
            nextPage = 2,
            prevPage = null,
            results = listOf(CharacterModelMother.mockRick())
        )
        val page2 = CharactersPageModel(
            count = 2,
            pages = 2,
            nextPage = null,
            prevPage = 1,
            results = listOf(CharacterModelMother.mockMorty())
        )

        whenever(useCase.invoke(page = eq(1), filters = any()))
            .thenReturn(DataResult.Success(page1))
        whenever(useCase.invoke(page = eq(2), filters = any()))
            .thenReturn(DataResult.Success(page2))

        viewModel.updateName("Rick")
        viewModel.search()
        advanceUntilIdle()

        viewModel.loadMore()
        advanceUntilIdle()

        val state = viewModel.uiState.value as AdvancedSearchUiState.Success
        assertEquals(2, state.characters.size)
        assertEquals(false, state.hasMorePages)
    }

    @Test
    fun `loadMore when no more pages does nothing`() = runTest {
        val page = CharactersPageModelMother.mock().copy(nextPage = null)

        whenever(useCase.invoke(page = eq(1), filters = any()))
            .thenReturn(DataResult.Success(page))

        viewModel.updateName("Rick")
        viewModel.search()
        advanceUntilIdle()

        viewModel.loadMore()
        advanceUntilIdle()

        verify(useCase, times(1))
            .invoke(eq(1), any())
    }

    @Test
    fun `loadMore with error keeps existing characters`() = runTest {
        val page1 = CharactersPageModelMother.mock()
        val errorResult = DataResult.Error(AppError.Network(cause = Exception("Error")))

        whenever(useCase.invoke(page = eq(1), filters = any()))
            .thenReturn(DataResult.Success(page1))
        whenever(useCase.invoke(page = eq(2), filters = any()))
            .thenReturn(errorResult)

        viewModel.updateName("Rick")
        viewModel.search()
        advanceUntilIdle()

        viewModel.loadMore()
        advanceUntilIdle()

        val state = viewModel.uiState.value as AdvancedSearchUiState.Success
        assertEquals(page1.results.size, state.characters.size)
        assertEquals(false, state.hasMorePages)
    }
}