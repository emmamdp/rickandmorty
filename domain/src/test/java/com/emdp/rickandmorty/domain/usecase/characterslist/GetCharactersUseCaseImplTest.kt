package com.emdp.rickandmorty.domain.usecase.characterslist

import com.emdp.rickandmorty.core.common.result.DataResult
import com.emdp.rickandmorty.domain.models.CharacterModel
import com.emdp.rickandmorty.domain.models.RickAndMortyPagedData
import com.emdp.rickandmorty.domain.repository.CharactersRepository
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoMoreInteractions
import org.mockito.kotlin.whenever

internal class GetCharactersUseCaseImplTest {

    private val repository: CharactersRepository = mock()
    private val useCase: GetCharactersUseCase = GetCharactersUseCaseImpl(repository)

    @Test
    fun `invoke delegates to repository with same non-null filter and returns same result`() =
        runTest {
            val page = 1
            val filter = CharactersFilterModelMother.mock()
            val expectedResult: DataResult<RickAndMortyPagedData<CharacterModel>> = DataResult.Success(
                RickAndMortyPagedData(
                    data = emptyList(),
                    page = page,
                    hasMore = false
                )
            )

            whenever(repository.getCharactersPaged(page, filter))
                .thenReturn(expectedResult)

            val result = useCase(page, filter)

            assertSame(expectedResult, result)
            verify(repository, times(1))
                .getCharactersPaged(page, filter)
            verifyNoMoreInteractions(repository)
        }

    @Test
    fun `invoke delegates to repository with null filter and returns same result`() = runTest {
        val page = 1
        val expectedResult: DataResult<RickAndMortyPagedData<CharacterModel>> = DataResult.Success(
            RickAndMortyPagedData(
                data = emptyList(),
                page = page,
                hasMore = false
            )
        )

        whenever(repository.getCharactersPaged(page, null))
            .thenReturn(expectedResult)

        val result = useCase.invoke(page, null)

        assertSame(expectedResult, result)
        verify(repository, times(1))
            .getCharactersPaged(page, null)
        verifyNoMoreInteractions(repository)
    }
}