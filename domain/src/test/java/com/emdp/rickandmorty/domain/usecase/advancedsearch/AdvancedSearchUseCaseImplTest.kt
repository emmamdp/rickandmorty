package com.emdp.rickandmorty.domain.usecase.advancedsearch

import com.emdp.rickandmorty.core.common.result.AppError
import com.emdp.rickandmorty.core.common.result.DataResult
import com.emdp.rickandmorty.domain.models.CharactersFilterModelMother
import com.emdp.rickandmorty.domain.models.CharactersPageModelMother
import com.emdp.rickandmorty.domain.repository.CharactersRepository
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.kotlin.whenever

internal class AdvancedSearchUseCaseImplTest {

    private val repository: CharactersRepository = mock()
    private val useCase = AdvancedSearchUseCaseImpl(repository)

    @Test
    fun `invoke returns Success with CharactersPageModel`() = runTest {
        val filters = CharactersFilterModelMother.mock()
        val expectedPage = CharactersPageModelMother.mock()
        val expected = DataResult.Success(data = expectedPage)

        whenever(repository.searchCharacters(page = 1, filters = filters))
            .thenReturn(expected)

        val result = useCase.invoke(page = 1, filters = filters)

        assertTrue(result is DataResult.Success)
        assertEquals(expectedPage, (result as DataResult.Success).data)
        verify(repository, times(1))
            .searchCharacters(page = 1, filters = filters)
    }

    @Test
    fun `invoke returns Error when repository fails`() = runTest {
        val filters = CharactersFilterModelMother.mockEmpty()
        val expected = DataResult.Error(
            error = AppError.Network(cause = Exception("Connection error"))
        )

        whenever(repository.searchCharacters(page = 1, filters = filters))
            .thenReturn(expected)

        val result = useCase.invoke(page = 1, filters = filters)

        assertTrue(result is DataResult.Error)
        assertEquals(expected.error, (result as DataResult.Error).error)
        verify(repository, times(1))
            .searchCharacters(page = 1, filters = filters)
    }

    @Test
    fun `invoke with page 3 calls repository with correct page`() = runTest {
        val filters = CharactersFilterModelMother.mockOnlyName("Summer")
        val expectedPage = CharactersPageModelMother.mock()
        val expected = DataResult.Success(data = expectedPage)

        whenever(repository.searchCharacters(page = 3, filters = filters))
            .thenReturn(expected)

        val result = useCase.invoke(page = 3, filters = filters)

        assertTrue(result is DataResult.Success)
        verify(repository, times(1))
            .searchCharacters(page = 3, filters = filters)
    }

    @Test
    fun `invoke returns Http error when API returns error code`() = runTest {
        val filters = CharactersFilterModelMother.mock()
        val expected = DataResult.Error(error = AppError.Http(code = 404, message = "Not found"))

        whenever(repository.searchCharacters(page = 1, filters = filters))
            .thenReturn(expected)

        val result = useCase.invoke(page = 1, filters = filters)

        assertTrue(result is DataResult.Error)
        val error = (result as DataResult.Error).error
        assertTrue(error is AppError.Http)
        assertEquals(404, (error as AppError.Http).code)
        verify(repository, times(1))
            .searchCharacters(page = 1, filters = filters)
    }
}