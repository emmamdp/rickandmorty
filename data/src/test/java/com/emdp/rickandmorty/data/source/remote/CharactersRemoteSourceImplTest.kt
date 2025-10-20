package com.emdp.rickandmorty.data.source.remote

import com.emdp.rickandmorty.core.common.result.AppError
import com.emdp.rickandmorty.core.common.result.DataResult
import com.emdp.rickandmorty.data.source.remote.api.CharactersApi
import com.emdp.rickandmorty.data.source.remote.dto.CharactersResponseDtoMother
import com.emdp.rickandmorty.data.source.remote.mapper.CharactersRemoteMapper
import com.emdp.rickandmorty.domain.models.CharacterModel
import com.emdp.rickandmorty.domain.models.CharacterModelMother
import com.emdp.rickandmorty.domain.models.CharactersFilterModel
import com.emdp.rickandmorty.domain.models.RickAndMortyPagedData
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

internal class CharactersRemoteSourceImplTest {

    private val api: CharactersApi = mock()
    private val mapper: CharactersRemoteMapper = mock()

    private val source: CharactersRemoteSource = CharactersRemoteSourceImpl(api, mapper)

    @Test
    fun `getCharactersPaged returns Success and maps response`() = runTest {
        val response = CharactersResponseDtoMother.mock()
        val pagedData = RickAndMortyPagedData(
            data = listOf(CharacterModelMother.mockRick()),
            page = PAGE_1,
            hasMore = true,
            totalPages = 10
        )
        val filter = CharactersFilterModel(
            name = NAME_RICK,
            status = STATUS_ALIVE,
            species = HUMAN,
            type = null,
            gender = GENDER_MALE
        )

        whenever(
            api.getCharacters(
                page = PAGE_1,
                name = NAME_RICK,
                status = STATUS_ALIVE,
                species = HUMAN,
                type = null,
                gender = GENDER_MALE
            )
        ).thenReturn(response)
        whenever(mapper.toModel(response = response, requestedPage = PAGE_1)).thenReturn(pagedData)

        val result = source.getCharactersPaged(page = PAGE_1, filter = filter)

        assertTrue(result is DataResult.Success)
        assertEquals(pagedData, (result as DataResult.Success).data)
        verify(api, times(1)).getCharacters(
            PAGE_1,
            NAME_RICK,
            STATUS_ALIVE,
            HUMAN,
            null,
            GENDER_MALE
        )
        verify(mapper, times(1))
            .toModel(response = response, requestedPage = PAGE_1)
    }

    @Test
    fun `getCharactersPaged with null filter passes nulls to api`() = runTest {
        val response = CharactersResponseDtoMother.mock()
        val pagedData = RickAndMortyPagedData<CharacterModel>(
            data = emptyList(),
            page = PAGE_1,
            hasMore = false,
            totalPages = 1
        )

        whenever(
            api.getCharacters(
                PAGE_1,
                null,
                null,
                null,
                null,
                null
            )
        ).thenReturn(response)
        whenever(mapper.toModel(response = response, requestedPage = PAGE_1)).thenReturn(pagedData)

        val result = source.getCharactersPaged(page = PAGE_1, filter = null)

        assertTrue(result is DataResult.Success)
        verify(api, times(1))
            .getCharacters(PAGE_1, null, null, null, null, null)
    }

    @Test
    fun `getCharactersPaged returns Error using mapper toError on IOException`() = runTest {
        val io = IOException("timeout")
        val expected = AppError.Network(io)

        whenever(
            api.getCharacters(
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull()
            )
        ).thenAnswer { throw io }
        whenever(mapper.toError(io)).thenReturn(expected)

        val result = source.getCharactersPaged(page = PAGE_1, filter = null)

        assertTrue(result is DataResult.Error)
        assertEquals(expected, (result as DataResult.Error).error)
        verify(mapper, times(1)).toError(io)
    }

    @Test
    fun `getCharactersPaged returns Error using mapper toError on HttpException`() = runTest {
        val body = """{"error":"Not found"}""".toResponseBody("application/json".toMediaType())
        val http = HttpException(Response.error<Any>(404, body))
        val expected = AppError.Http(404, http.message())
        val filter = CharactersFilterModel(
            name = "x",
            status = "dead",
            species = HUMAN,
            type = null,
            gender = "male"
        )

        whenever(
            api.getCharacters(
                page = 9,
                name = "x",
                status = "dead",
                species = HUMAN,
                type = null,
                gender = "male"
            )
        ).thenThrow(http)
        whenever(mapper.toError(http)).thenReturn(expected)

        val result = source.getCharactersPaged(page = 9, filter = filter)

        assertTrue(result is DataResult.Error)
        val err = (result as DataResult.Error).error
        assertTrue(err is AppError.Http)
        assertEquals(404, (err as AppError.Http).code)
        verify(mapper, times(1)).toError(http)
    }

    @Test
    fun `getCharactersPaged with page 2 calls api with correct page`() = runTest {
        val response = CharactersResponseDtoMother.mock()
        val pagedData = RickAndMortyPagedData<CharacterModel>(
            data = emptyList(),
            page = 2,
            hasMore = true,
            totalPages = 5
        )

        whenever(
            api.getCharacters(
                2,
                null,
                null,
                null,
                null,
                null
            )
        ).thenReturn(response)
        whenever(mapper.toModel(response = response, requestedPage = 2))
            .thenReturn(pagedData)

        val result = source.getCharactersPaged(page = 2, filter = null)

        assertTrue(result is DataResult.Success)
        verify(api, times(1))
            .getCharacters(2, null, null, null, null, null)
    }

    companion object {
        private const val NAME_RICK = "rick"
        private const val STATUS_ALIVE = "alive"
        private const val HUMAN = "human"
        private const val GENDER_MALE = "male"
        private const val PAGE_1 = 1
    }
}