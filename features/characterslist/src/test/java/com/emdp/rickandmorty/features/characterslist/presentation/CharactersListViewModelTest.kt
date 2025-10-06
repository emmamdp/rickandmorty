package com.emdp.rickandmorty.features.characterslist.presentation

import androidx.paging.PagingData
import com.emdp.rickandmorty.domain.usecase.characterslist.GetCharactersUseCase
import com.emdp.rickandmorty.features.characterslist.domain.models.CharactersFilterModelMother
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.kotlin.anyOrNull
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
        viewModel = CharactersListViewModel(getCharactersUseCase = getCharactersUseCase)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial filterState is null`() = runTest {
        val initialFilter = viewModel.filterState.value

        assertNull(initialFilter)
    }

    @Test
    fun `characters flow invokes use case with null filter initially`() = runTest {
        whenever(getCharactersUseCase.invoke(null))
            .thenReturn(flowOf(PagingData.empty()))

        val job = launch {
            viewModel.characters.collect { }
        }

        testDispatcher.scheduler.advanceUntilIdle()

        verify(getCharactersUseCase, times(1)).invoke(null)

        job.cancel()
    }

    @Test
    fun `applyFilter updates filterState`() = runTest {
        val filter = CharactersFilterModelMother.mock()

        viewModel.applyFilter(filter)

        assertEquals(filter, viewModel.filterState.value)
    }

    @Test
    fun `applyFilter triggers new use case invocation`() = runTest {
        val filter = CharactersFilterModelMother.mock()

        whenever(getCharactersUseCase.invoke(anyOrNull()))
            .thenReturn(flowOf(PagingData.empty()))

        val job = launch {
            viewModel.characters.collect { }
        }

        testDispatcher.scheduler.advanceUntilIdle()
        verify(getCharactersUseCase, times(1)).invoke(null)

        viewModel.applyFilter(filter)
        testDispatcher.scheduler.advanceUntilIdle()

        verify(getCharactersUseCase, times(1)).invoke(eq(filter))

        job.cancel()
    }

    @Test
    fun `clearFilter resets filterState to null`() = runTest {
        val filter = CharactersFilterModelMother.mock()

        viewModel.applyFilter(filter)
        assertEquals(filter, viewModel.filterState.value)

        viewModel.clearFilter()

        assertNull(viewModel.filterState.value)
    }

    @Test
    fun `clearFilter after non-null filter invokes use case with null`() = runTest {
        val filter = CharactersFilterModelMother.mock()

        whenever(getCharactersUseCase.invoke(anyOrNull()))
            .thenReturn(flowOf(PagingData.empty()))

        val job = launch {
            viewModel.characters.collect { }
        }

        testDispatcher.scheduler.advanceUntilIdle()
        verify(getCharactersUseCase, times(1)).invoke(null)

        viewModel.applyFilter(filter)
        testDispatcher.scheduler.advanceUntilIdle()
        verify(getCharactersUseCase, times(1)).invoke(eq(filter))

        viewModel.clearFilter()
        testDispatcher.scheduler.advanceUntilIdle()

        verify(getCharactersUseCase, times(2)).invoke(null)

        job.cancel()
    }
}