package com.emdp.rickandmorty.features.characterslist.presentation

import androidx.paging.PagingData
import com.emdp.rickandmorty.domain.models.CharacterModel
import com.emdp.rickandmorty.domain.usecase.characterslist.GetCharactersUseCase
import com.emdp.rickandmorty.features.characterslist.domain.models.CharactersFilterModelMother
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
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
    fun `characters initial subscription invokes use case with null filter`() =
        runTest {
            whenever(getCharactersUseCase.invoke(null))
                .thenReturn(flowOf(PagingData.empty()))

            val emitted = viewModel.characters.first()

            verify(getCharactersUseCase, times(1)).invoke(null)
            assertSame(emitted::class, PagingData.empty<CharacterModel>()::class)
        }

    @Test
    fun `applyFilter delegates to use case with the same filter`() = runTest {
        val filter = CharactersFilterModelMother.mock()

        whenever(getCharactersUseCase.invoke(eq(filter)))
            .thenReturn(flowOf(PagingData.empty()))

        viewModel.applyFilter(filter)
        viewModel.characters.first()

        verify(getCharactersUseCase, times(1)).invoke(eq(filter))
    }

    @Test
    fun `clearFilter after non-null filter delegates to use case with null`() = runTest {
        val filter = CharactersFilterModelMother.mockMortyWithNulls()

        whenever(getCharactersUseCase.invoke(anyOrNull()))
            .thenReturn(flowOf(PagingData.empty()))

        viewModel.applyFilter(filter)

        val job = launch { viewModel.characters.collect { } }

        testDispatcher.scheduler.advanceUntilIdle()
        verify(getCharactersUseCase, times(1)).invoke(eq(filter))

        viewModel.clearFilter()
        testDispatcher.scheduler.advanceUntilIdle()

        verify(getCharactersUseCase, times(1)).invoke(null)

        job.cancel()
    }

    @Test
    fun `loadInitial sets flag to true only once`() = runTest {
        val field = CharactersListViewModel::class.java
            .getDeclaredField("hasLoadedInitially")
            .apply { isAccessible = true }

        assertFalse(field.getBoolean(viewModel))

        viewModel.loadInitial()
        assertTrue(field.getBoolean(viewModel))

        viewModel.loadInitial()
        assertTrue(field.getBoolean(viewModel))
    }
}