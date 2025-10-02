package com.emdp.rickandmorty.domain.usecase.characterslist

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

internal class GetCharactersUseCaseImplTest {

    private val repository: CharactersRepository = Mockito.mock(CharactersRepository::class.java)
    private val useCase: GetCharactersUseCase = GetCharactersUseCaseImpl(repository)

    @Test
    fun `invoke returns Success and delegates filters to repository`() = runTest {
        val params = GetCharactersUseCase.Params(
            page = 2,
            name = "rick",
            status = "alive",
            species = "human",
            type = null,
            gender = "male"
        )
        val list = CharacterModelMother.mockList()
        val expected = DataResult.Success(list)

        whenever(
            repository.getCharacters(
                page = params.page,
                name = params.name,
                status = params.status,
                species = params.species,
                type = params.type,
                gender = params.gender
            )
        ).thenReturn(expected)

        val result = useCase(params)

        assertTrue(result is DataResult.Success)
        assertEquals(list, (result as DataResult.Success).data)
        verify(repository).getCharacters(
            page = params.page,
            name = params.name,
            status = params.status,
            species = params.species,
            type = params.type,
            gender = params.gender
        )
    }

    @Test
    fun `invoke returns Error when repository fails`() = runTest {
        val params = GetCharactersUseCase.Params()
        val expected = DataResult.Error(AppError.Network(Exception("timeout")))

        whenever(
            repository.getCharacters(
                page = null, name = null, status = null, species = null, type = null, gender = null
            )
        ).thenReturn(expected)

        val result = useCase(params)

        assertTrue(result is DataResult.Error)
        assertEquals(expected.error, (result as DataResult.Error).error)
        verify(repository).getCharacters(
            page = null, name = null, status = null, species = null, type = null, gender = null
        )
    }
}