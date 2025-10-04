package com.emdp.rickandmorty.features.characterdetail.presentation

import com.emdp.rickandmorty.core.common.result.AppError
import com.emdp.rickandmorty.core.common.result.DataResult
import com.emdp.rickandmorty.domain.models.CharacterModel
import com.emdp.rickandmorty.domain.usecase.character.GetCharacterUseCase
import com.emdp.rickandmorty.features.characterdetail.presentation.CharacterDetailUiState.Content
import com.emdp.rickandmorty.features.characterdetail.presentation.CharacterDetailUiState.Error
import com.emdp.rickandmorty.features.characterdetail.presentation.mapper.CharacterDetailMapper
import com.emdp.rickandmorty.features.characterdetail.presentation.uimodel.CharacterDetailUiModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.clearInvocations
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
internal class CharacterDetailViewModelTest {

    private val useCase: GetCharacterUseCase = mock()
    private val mapper: CharacterDetailMapper = mock()

    private lateinit var viewModel: CharacterDetailViewModel

    private val testDispatcher = StandardTestDispatcher()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = CharacterDetailViewModel(
            getCharacterUseCase = useCase,
            mapper = mapper
        )
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `load with invalid id emits Error and does not invoke dependencies`() =
        runTest(testDispatcher) {
            viewModel.load(0)

            assertTrue(viewModel.uiState.value is Error)
            verifyNoInteractions(useCase)
            verifyNoInteractions(mapper)
        }

    @Test
    fun `load success emits Content with mapped UiModel`() =
        runTest(testDispatcher) {
            val id = 1
            val params = GetCharacterUseCase.Params(id)
            val domainModel: CharacterModel = mock()
            val uiModel: CharacterDetailUiModel = mock()

            whenever(useCase(params)).thenReturn(DataResult.Success(domainModel))
            whenever(mapper.getUiModel(domainModel)).thenReturn(uiModel)

            viewModel.load(id)
            advanceUntilIdle()

            val state = viewModel.uiState.value
            assertTrue(state is Content)
            assertSame(uiModel, (state as Content).uiModel)
            verify(useCase).invoke(params)
            verify(mapper).getUiModel(domainModel)
            verifyNoMoreInteractions(useCase, mapper)
        }

    @Test
    fun `load error emits Error and does not map`() = runTest(testDispatcher) {
        val id = 2
        val params = GetCharacterUseCase.Params(id)
        whenever(useCase(params)).thenReturn(DataResult.Error(AppError.DataNotFound))

        viewModel.load(id)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value is Error)
        verify(useCase).invoke(params)
        verify(mapper, never()).getUiModel(any())
    }

    @Test
    fun `retry after error re-invokes use case and emits Content`() =
        runTest(testDispatcher) {
            val id = 3
            val params = GetCharacterUseCase.Params(id)
            val domainModel: CharacterModel = mock()
            val uiModel: CharacterDetailUiModel = mock()

            whenever(useCase(params))
                .thenReturn(DataResult.Error(AppError.DataNotFound))
                .thenReturn(DataResult.Success(domainModel))
            whenever(mapper.getUiModel(domainModel)).thenReturn(uiModel)

            viewModel.load(id)
            advanceUntilIdle()
            assertTrue(viewModel.uiState.value is Error)

            viewModel.retry()
            advanceUntilIdle()

            val state = viewModel.uiState.value
            assertTrue(state is Content)
            assertSame(uiModel, (state as Content).uiModel)
            verify(useCase, times(2)).invoke(params)
            verify(mapper).getUiModel(domainModel)
        }

    @Test
    fun `load with same id when already in Content does not reload`() =
        runTest(testDispatcher) {
            val id = 4
            val params = GetCharacterUseCase.Params(id)
            val domainModel: CharacterModel = mock()
            val uiModel: CharacterDetailUiModel = mock()

            whenever(useCase(params)).thenReturn(DataResult.Success(domainModel))
            whenever(mapper.getUiModel(domainModel)).thenReturn(uiModel)

            viewModel.load(id)
            advanceUntilIdle()
            assertTrue(viewModel.uiState.value is Content)

            clearInvocations(useCase, mapper)

            viewModel.load(id)
            advanceUntilIdle()

            verifyNoInteractions(useCase)
            verifyNoInteractions(mapper)
            assertTrue(viewModel.uiState.value is Content)
        }
}
