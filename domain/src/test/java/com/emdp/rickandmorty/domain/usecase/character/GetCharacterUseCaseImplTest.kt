package com.emdp.rickandmorty.domain.usecase.character

import com.emdp.rickandmorty.core.common.result.AppError
import com.emdp.rickandmorty.core.common.result.DataResult
import com.emdp.rickandmorty.domain.models.CharacterModelMother
import com.emdp.rickandmorty.domain.repository.CharactersRepository
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.verify
import org.mockito.kotlin.whenever

internal class GetCharacterUseCaseImplTest {

    private val repository: CharactersRepository = Mockito.mock(CharactersRepository::class.java)
    private val useCase: GetCharacterUseCase = GetCharacterUseCaseImpl(repository)

    @Test
    fun `invoke returns Success when repository succeeds`() = runTest {
        val model = CharacterModelMother.mockRick()

        whenever(repository.getCharacterById(1))
            .thenReturn(DataResult.Success(model))

        val result = useCase(GetCharacterUseCase.Params(id = 1))

        assertTrue(result is DataResult.Success)
        assertEquals(model, (result as DataResult.Success).data)
        verify(repository).getCharacterById(1)
    }

    @Test
    fun `invoke returns Error when repository fails`() = runTest {
        val error = DataResult.Error(AppError.Unexpected(IllegalStateException("boom")))
        whenever(repository.getCharacterById(42)).thenReturn(error)

        val result = useCase(GetCharacterUseCase.Params(id = 42))

        assertTrue(result is DataResult.Error)
        assertEquals(error.error, (result as DataResult.Error).error)
        verify(repository).getCharacterById(42)
    }
}