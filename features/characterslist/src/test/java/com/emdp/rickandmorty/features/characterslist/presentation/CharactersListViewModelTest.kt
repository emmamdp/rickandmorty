package com.emdp.rickandmorty.features.characterslist.presentation

import com.emdp.rickandmorty.core.common.result.AppError
import com.emdp.rickandmorty.core.common.result.DataResult
import com.emdp.rickandmorty.domain.models.CharacterModel
import com.emdp.rickandmorty.domain.models.CharactersPageModel
import com.emdp.rickandmorty.domain.usecase.characterslist.GetCharactersUseCase
import com.emdp.rickandmorty.features.characterslist.domain.models.CharacterModelMother
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
internal class CharactersListViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: CharactersListViewModel
    private val getCharactersUseCase: GetCharactersUseCase = mock()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = CharactersListViewModel(
            getCharactersUseCase = getCharactersUseCase
        )
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadInitial success updates state with items and stops loading`() = runTest {
        val domainItems = CharacterModelMother.mockList()
        val page = dummyPage(domainItems)
        whenever(getCharactersUseCase.invoke(eq(GetCharactersUseCase.Params(page = 1))))
            .thenReturn(DataResult.Success(page))

        viewModel.loadInitial()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.first()
        assertEquals(false, state.isLoading)
        assertEquals(null, state.error)
        assertEquals(2, state.items.size)
        assertEquals("Rick", state.items[0].name)
    }

    @Test
    fun `loadInitial error updates state with error and stops loading`() = runTest {
        val error = AppError.Network()
        whenever(getCharactersUseCase.invoke(any()))
            .thenReturn(DataResult.Error(error))

        viewModel.loadInitial()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.first()
        assertEquals(false, state.isLoading)
        assertEquals(emptyList<CharacterModel>(), state.items)
        assertEquals(error, state.error)
    }

    @Test
    fun `retry re-executes last request`() = runTest {
        val error = AppError.Unexpected()
        whenever(getCharactersUseCase.invoke(eq(GetCharactersUseCase.Params(page = 1))))
            .thenReturn(DataResult.Error(error))

        viewModel.loadInitial()
        testDispatcher.scheduler.advanceUntilIdle()

        val domainItems = CharacterModelMother.mockListWithOneCharacter()
        val page = dummyPage(domainItems)
        whenever(getCharactersUseCase.invoke(eq(GetCharactersUseCase.Params(page = 1))))
            .thenReturn(DataResult.Success(page))

        viewModel.retry()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.first()
        assertEquals(false, state.isLoading)
        assertEquals(null, state.error)
        assertEquals(1, state.items.size)
        assertEquals(3, state.items.first().id)
    }

    private fun dummyPage(items: List<CharacterModel>) =
        CharactersPageModel(
            count = 20,
            pages = 4,
            nextPage = 2,
            prevPage = 1,
            results = items
        )
}