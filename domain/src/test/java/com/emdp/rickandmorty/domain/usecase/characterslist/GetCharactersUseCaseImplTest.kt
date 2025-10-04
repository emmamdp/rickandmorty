package com.emdp.rickandmorty.domain.usecase.characterslist

import androidx.paging.PagingData
import com.emdp.rickandmorty.domain.models.CharacterModel
import com.emdp.rickandmorty.domain.repository.CharactersRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoMoreInteractions
import org.mockito.kotlin.whenever

internal class GetCharactersUseCaseImplTest {

    private val repository: CharactersRepository = mock(CharactersRepository::class.java)
    private val useCase: GetCharactersUseCase = GetCharactersUseCaseImpl(repository)

    @Test
    fun `invoke delegates to repository with same non-null filter and returns same flow`() =
        runTest {
            val filter = CharactersFilterModelMother.mock()
            val expectedFlow: Flow<PagingData<CharacterModel>> = flowOf(PagingData.empty())

            whenever(repository.getCharactersPaged(filter)).thenReturn(expectedFlow)

            val result = useCase(filter)

            assertSame(expectedFlow, result)
            verify(repository, times(1)).getCharactersPaged(filter)
            verifyNoMoreInteractions(repository)
        }

    @Test
    fun `invoke delegates to repository with null filter and returns same flow`() = runTest {
        val expectedFlow: Flow<PagingData<CharacterModel>> = flowOf(PagingData.empty())

        whenever(repository.getCharactersPaged(null)).thenReturn(expectedFlow)

        val result = useCase.invoke(null)

        assertSame(expectedFlow, result)
        verify(repository, times(1)).getCharactersPaged(null)
        verifyNoMoreInteractions(repository)
    }
}