package com.emdp.rickandmorty.features.advancedsearch.presentation

import com.emdp.rickandmorty.core.common.result.AppError
import com.emdp.rickandmorty.core.common.result.DataResult
import com.emdp.rickandmorty.domain.models.CharacterModel
import com.emdp.rickandmorty.domain.models.RickAndMortyPagedData
import com.emdp.rickandmorty.domain.usecase.characterslist.GetCharactersUseCase
import com.emdp.rickandmorty.features.advancedsearch.models.CharacterModelMother
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoInteractions
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.whenever
import java.util.stream.Stream

@OptIn(ExperimentalCoroutinesApi::class)
internal class RickAndMortyAdvancedSearchViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val useCase: GetCharactersUseCase = mock()
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

    @ParameterizedTest(name = "update{0} updates filter and triggers search")
    @MethodSource("filterUpdateProvider")
    fun `update filter triggers search`(
        filterType: String,
        filterValue: String,
        expectedValue: String
    ) = runTest {
        val pagedData = RickAndMortyPagedData(
            data = listOf(CharacterModelMother.mockRick()),
            page = 1,
            hasMore = true,
            totalPages = 5
        )
        mockSuccessResponse(pagedData)

        when (filterType) {
            FILTER_STATUS -> viewModel.updateStatus(filterValue)
            FILTER_SPECIES -> viewModel.updateSpecies(filterValue)
            FILTER_GENDER -> viewModel.updateGender(filterValue)
        }
        advanceUntilIdle()

        val filters = viewModel.filters.value
        when (filterType) {
            FILTER_STATUS -> assertEquals(expectedValue, filters.status)
            FILTER_SPECIES -> assertEquals(expectedValue, filters.species)
            FILTER_GENDER -> assertEquals(expectedValue, filters.gender)
        }
        assertTrue(viewModel.uiState.value is AdvancedSearchUiState.Success)
        verify(useCase, times(1))
            .invoke(page = eq(1), filter = any())
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
        val pagedData = RickAndMortyPagedData(
            data = listOf(CharacterModelMother.mockRick(), CharacterModelMother.mockMorty()),
            page = 1,
            hasMore = true,
            totalPages = 5
        )
        mockSuccessResponse(pagedData)

        performSearchWithNameAndWait("Rick")

        val state = viewModel.uiState.value
        assertTrue(state is AdvancedSearchUiState.Success)
        assertEquals(pagedData.data.size, (state as AdvancedSearchUiState.Success).characters.size)
        assertTrue(state.hasMorePages)
    }

    @Test
    fun `search with no more pages returns Success with hasMorePages false`() = runTest {
        val pagedData = RickAndMortyPagedData(
            data = listOf(CharacterModelMother.mockRick()),
            page = 1,
            hasMore = false,
            totalPages = 1
        )
        mockSuccessResponse(pagedData)

        performSearchWithNameAndWait("Rick")

        val state = viewModel.uiState.value as AdvancedSearchUiState.Success
        assertEquals(false, state.hasMorePages)
    }

    @ParameterizedTest(name = "search with error returns Error state")
    @MethodSource("errorProvider")
    fun `search with error returns Error state`(
        error: AppError
    ) = runTest {
        mockErrorResponse(error)

        performSearchWithNameAndWait("Rick")

        assertTrue(viewModel.uiState.value is AdvancedSearchUiState.Error)
    }

    @Test
    fun `search with 404 error returns Success with empty list`() = runTest {
        mockErrorResponse(AppError.Http(code = 404, message = "Not found"))

        performSearchWithNameAndWait("Rick")

        val state = viewModel.uiState.value
        assertTrue(state is AdvancedSearchUiState.Success)
        val successState = state as AdvancedSearchUiState.Success
        assertTrue(successState.characters.isEmpty())
        assertEquals(false, successState.hasMorePages)
    }

    @Test
    fun `loadMore appends characters to existing list`() = runTest {
        val page1 = RickAndMortyPagedData(
            data = listOf(CharacterModelMother.mockRick()),
            page = 1,
            hasMore = true,
            totalPages = 2
        )
        val page2 = RickAndMortyPagedData(
            data = listOf(CharacterModelMother.mockMorty()),
            page = 2,
            hasMore = false,
            totalPages = 2
        )
        mockSuccessResponse(page1, page = 1)
        mockSuccessResponse(page2, page = 2)

        performSearchWithNameAndWait("Rick")
        performLoadMoreAndWait()

        val state = viewModel.uiState.value as AdvancedSearchUiState.Success
        assertEquals(2, state.characters.size)
        assertEquals(false, state.hasMorePages)
    }

    @Test
    fun `loadMore when no more pages does nothing`() = runTest {
        val pagedData = RickAndMortyPagedData(
            data = listOf(CharacterModelMother.mockRick()),
            page = 1,
            hasMore = false,
            totalPages = 1
        )
        mockSuccessResponse(pagedData)

        performSearchWithNameAndWait("Rick")
        performLoadMoreAndWait()

        verify(useCase, times(1)).invoke(eq(1), any())
    }

    @Test
    fun `loadMore with network error keeps existing characters`() = runTest {
        val page1 = RickAndMortyPagedData(
            data = listOf(CharacterModelMother.mockRick()),
            page = 1,
            hasMore = true,
            totalPages = 2
        )
        mockSuccessResponse(page1, page = 1)
        mockErrorResponse(AppError.Network(cause = Exception("Error")), page = 2)

        performSearchWithNameAndWait("Rick")
        performLoadMoreAndWait()

        val state = viewModel.uiState.value as AdvancedSearchUiState.Success
        assertEquals(page1.data.size, state.characters.size)
        assertEquals(false, state.hasMorePages)
    }

    @Test
    fun `loadMore with 404 error clears all characters`() = runTest {
        val page1 = RickAndMortyPagedData(
            data = listOf(CharacterModelMother.mockRick()),
            page = 1,
            hasMore = true,
            totalPages = 2
        )
        mockSuccessResponse(page1, page = 1)
        mockErrorResponse(AppError.Http(code = 404, message = "Not found"), page = 2)

        performSearchWithNameAndWait("Rick")
        performLoadMoreAndWait()

        val state = viewModel.uiState.value as AdvancedSearchUiState.Success
        assertTrue(state.characters.isEmpty())
        assertEquals(false, state.hasMorePages)
    }

    private suspend fun mockSuccessResponse(
        pagedData: RickAndMortyPagedData<CharacterModel>,
        page: Int = 1
    ) {
        whenever(useCase.invoke(page = eq(page), filter = any()))
            .thenReturn(DataResult.Success(pagedData))
    }

    private suspend fun mockErrorResponse(error: AppError, page: Int = 1) {
        whenever(useCase.invoke(page = eq(page), filter = any()))
            .thenReturn(DataResult.Error(error))
    }

    private fun performSearchWithName(name: String) {
        viewModel.updateName(name)
        viewModel.search()
    }

    private fun TestScope.performSearchWithNameAndWait(name: String) {
        performSearchWithName(name)
        advanceUntilIdle()
    }

    private fun TestScope.performLoadMoreAndWait() {
        viewModel.loadMore()
        advanceUntilIdle()
    }

    companion object {
        private const val FILTER_STATUS = "Status"
        private const val FILTER_SPECIES = "Species"
        private const val FILTER_GENDER = "Gender"
        private const val VALUE_ALIVE = "alive"
        private const val VALUE_HUMAN = "Human"
        private const val VALUE_MALE = "male"

        @JvmStatic
        fun filterUpdateProvider(): Stream<Arguments> = Stream.of(
            Arguments.of(FILTER_STATUS, VALUE_ALIVE, VALUE_ALIVE),
            Arguments.of(FILTER_SPECIES, VALUE_HUMAN, VALUE_HUMAN),
            Arguments.of(FILTER_GENDER, VALUE_MALE, VALUE_MALE)
        )

        @JvmStatic
        fun errorProvider(): Stream<Arguments> = Stream.of(
            Arguments.of(AppError.Network(cause = Exception("No internet"))),
            Arguments.of(AppError.Http(code = 500, message = "Server error")),
            Arguments.of(AppError.Serialization(cause = Exception("Invalid JSON"))),
            Arguments.of(AppError.Unexpected(cause = Exception("Unknown")))
        )
    }
}