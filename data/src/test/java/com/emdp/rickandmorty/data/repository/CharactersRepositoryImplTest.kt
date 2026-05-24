package com.emdp.rickandmorty.data.repository

import com.emdp.rickandmorty.core.common.result.DataResult
import com.emdp.rickandmorty.data.source.local.CharacterLocalSource
import com.emdp.rickandmorty.data.source.local.RickAndMortyDatabase
import com.emdp.rickandmorty.data.source.local.dao.CharactersDao
import com.emdp.rickandmorty.data.source.local.mapper.CharacterLocalMapper
import com.emdp.rickandmorty.data.source.remote.CharactersRemoteSource
import com.emdp.rickandmorty.domain.models.CharacterModelMother
import com.emdp.rickandmorty.domain.models.CharactersFilterModelMother
import com.emdp.rickandmorty.domain.models.CharactersPageModelMother
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.whenever

internal class CharactersRepositoryImplTest {

    private val database: RickAndMortyDatabase = mock()
    private val localSource: CharacterLocalSource = mock()
    private val charactersDao: CharactersDao = mock()
    private val remoteSource: CharactersRemoteSource = mock()
    private val mapper: CharacterLocalMapper = mock()

    private lateinit var repository: CharactersRepositoryImpl

    @BeforeEach
    fun setUp() = runTest {
        whenever(database.charactersDao()).thenReturn(charactersDao)
        whenever(charactersDao.countCharacters()).thenReturn(0)

        repository = CharactersRepositoryImpl(
            database = database,
            localSource = localSource,
            charactersDao = charactersDao,
            remoteSource = remoteSource,
            mapper = mapper
        )
    }

    @Test
    fun `getCharactersPaged triggers pagingSourceFactory with correctly mapped filters`() =
        runTest {
            val filter = CharactersFilterModelMother.mock()
            
            whenever(charactersDao.pagingSource(anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull()))
                .thenReturn(mock())

            repository.getCharactersPaged(filter).first()

            verify(charactersDao).pagingSource(
                name = filter.name,
                status = "alive",
                species = filter.species,
                type = filter.type,
                gender = "male"
            )
        }

    @Test
    fun `getCharactersPaged with blank filters calls DAO with nulls`() = runTest {
        val filter = CharactersFilterModelMother.mockBlank()
        
        whenever(charactersDao.pagingSource(anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull()))
            .thenReturn(mock())

        repository.getCharactersPaged(filter).first()

        verify(charactersDao).pagingSource(null, null, null, null, null)
    }

    @Test
    fun `getCharacterById returns Success and delegates to localSource`() = runTest {
        val expectedModel = CharacterModelMother.mockRick()
        val expected = DataResult.Success(expectedModel)

        whenever(localSource.getCharacterById(1)).thenReturn(expected)

        val result = repository.getCharacterById(1)

        assertTrue(result is DataResult.Success)
        assertEquals(expectedModel, (result as DataResult.Success).data)
        verify(localSource, times(1)).getCharacterById(1)
    }

    @Test
    fun `searchCharacters returns Success with CharactersPageModel`() = runTest {
        val filters = CharactersFilterModelMother.mock()
        val expectedPage = CharactersPageModelMother.mock()
        val expected = DataResult.Success(expectedPage)

        whenever(
            remoteSource.getCharacters(
                page = 1,
                name = filters.name,
                status = filters.status,
                species = filters.species,
                type = filters.type,
                gender = filters.gender
            )
        ).thenReturn(expected)

        val result = repository.searchCharacters(page = 1, filters = filters)

        assertTrue(result is DataResult.Success)
        assertEquals(expectedPage, (result as DataResult.Success).data)
    }
}
