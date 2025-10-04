package com.emdp.rickandmorty.data.repository

import androidx.paging.ExperimentalPagingApi
import com.emdp.rickandmorty.core.common.result.AppError
import com.emdp.rickandmorty.core.common.result.DataResult
import com.emdp.rickandmorty.data.source.local.CharacterLocalSource
import com.emdp.rickandmorty.data.source.local.dao.CharactersDao
import com.emdp.rickandmorty.data.source.local.mapper.CharacterLocalMapper
import com.emdp.rickandmorty.data.source.mediator.CharactersRemoteMediator
import com.emdp.rickandmorty.data.source.mediator.CharactersRemoteMediatorFactory
import com.emdp.rickandmorty.domain.models.CharacterModelMother
import com.emdp.rickandmorty.domain.models.CharactersFilterModelMother
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoInteractions
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.eq
import org.mockito.kotlin.isNull
import org.mockito.kotlin.whenever

@OptIn(ExperimentalPagingApi::class)
internal class CharactersRepositoryImplTest {

    private val localSource: CharacterLocalSource = mock()
    private val charactersDao: CharactersDao = mock()
    private val mediatorFactory: CharactersRemoteMediatorFactory = mock()
    private val localMapper: CharacterLocalMapper = mock()

    private val repository = CharactersRepositoryImpl(
        localSource,
        charactersDao,
        mediatorFactory,
        localMapper
    )

    @Test
    fun `getCharactersPaged creates mediator with the same filter`() = runTest {
        val filter = CharactersFilterModelMother.mock()

        val flow = repository.getCharactersPaged(filter)

        assertNotNull(flow)
        verify(mediatorFactory, times(1)).create(filter)
        verifyNoInteractions(localSource)
        verifyNoInteractions(charactersDao)
    }

    @Test
    fun `getCharactersPaged invokes dao pagingSource with filter`() = runTest {
        val mediator = mock(CharactersRemoteMediator::class.java)
        val filter = CharactersFilterModelMother.mock()

        whenever(
            charactersDao.pagingSource(
                name = anyOrNull(),
                status = anyOrNull(),
                species = anyOrNull(),
                type = anyOrNull(),
                gender = anyOrNull()
            )
        ).thenReturn(EmptyPagingSource())
        whenever(mediatorFactory.create(filter)).thenReturn(mediator)

        repository.getCharactersPaged(filter).first()

        verify(mediatorFactory, times(1)).create(filter)
        verify(charactersDao, times(1)).pagingSource(
            name = eq(RICK),
            status = eq(ALIVE),
            species = eq(HUMAN),
            type = isNull(),
            gender = eq(GENDER_MALE)
        )
        verifyNoInteractions(localSource)
    }

    @Test
    fun `getCharacterById returns Success and delegates to remote`() = runTest {
        val expectedModel = CharacterModelMother.mockRick()
        val expected = DataResult.Success(expectedModel)

        whenever(localSource.getCharacterById(1)).thenReturn(expected)

        val result = repository.getCharacterById(1)

        assertTrue(result is DataResult.Success)
        assertEquals(expectedModel, (result as DataResult.Success).data)
        verify(localSource, times(1)).getCharacterById(1)
    }

    @Test
    fun `getCharacterById returns Error when remote fails`() = runTest {
        val expected = DataResult.Error(AppError.Unexpected(IllegalStateException("boom")))
        whenever(localSource.getCharacterById(42)).thenReturn(expected)

        val result = repository.getCharacterById(42)

        assertTrue(result is DataResult.Error)
        assertEquals(expected.error, (result as DataResult.Error).error)
        verify(localSource, times(1)).getCharacterById(42)
    }

    companion object {
        private const val RICK = "Rick"
        private const val ALIVE = "Alive"
        private const val HUMAN = "Human"
        private const val GENDER_MALE = "Male"
    }
}