package com.emdp.rickandmorty.data.source.remote

import com.emdp.rickandmorty.core.common.result.AppError
import com.emdp.rickandmorty.core.common.result.DataResult
import com.emdp.rickandmorty.data.source.remote.api.CharactersApi
import com.emdp.rickandmorty.data.source.remote.dto.CharacterDtoMother
import com.emdp.rickandmorty.data.source.remote.dto.CharactersResponseDtoMother
import com.emdp.rickandmorty.data.source.remote.mapper.CharactersRemoteMapper
import com.emdp.rickandmorty.domain.models.CharacterModelMother
import com.emdp.rickandmorty.domain.models.CharactersPageModelMother
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.whenever
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

internal class CharactersRemoteSourceImplTest {

    private val api: CharactersApi = Mockito.mock(CharactersApi::class.java)
    private val mapper: CharactersRemoteMapper = Mockito.mock(CharactersRemoteMapper::class.java)
    private val source: CharactersRemoteSource = CharactersRemoteSourceImpl(api, mapper)

    @Test
    fun `getCharacters returns Success and maps list`() = runTest {
        val response = CharactersResponseDtoMother.mock()
        val model = CharactersPageModelMother.mock()

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
        whenever(mapper.toModel(response)).thenReturn(model)

        val result = source.getCharacters(
            page = PAGE_1,
            name = NAME_RICK,
            status = STATUS_ALIVE,
            species = HUMAN,
            type = null,
            gender = GENDER_MALE
        )

        assertTrue(result is DataResult.Success)
        assertEquals(model, (result as DataResult.Success).data)
        verify(api, times(1)).getCharacters(
            PAGE_1,
            NAME_RICK,
            STATUS_ALIVE,
            HUMAN,
            null,
            GENDER_MALE
        )
        verify(mapper, times(1)).toModel(response)
    }

    @Test
    fun `getCharacters returns Error using mapper toError on IOException`() = runTest {
        val io = IOException("timeout")
        val expected = AppError.Network(io)

        doAnswer { throw io }.whenever(api).getCharacters(null, null, null, null, null, null)
        whenever(mapper.toError(io)).thenReturn(expected)

        val result = source.getCharacters(
            page = null, name = null, status = null, species = null, type = null, gender = null
        )

        assertTrue(result is DataResult.Error)
        assertEquals(expected, (result as DataResult.Error).error)
        verify(api, times(1)).getCharacters(null, null, null, null, null, null)
        verify(mapper, times(1)).toError(io)
    }

    @Test
    fun `getCharacters returns Error using mapper toError on HttpException`() = runTest {
        val body = """{"error":"Not found"}""".toResponseBody("application/json".toMediaType())
        val http = HttpException(Response.error<Any>(404, body))
        val expected = AppError.Http(404, http.message())

        whenever(
            api.getCharacters(
                page = anyInt(),
                name = anyString(),
                status = anyString(),
                species = anyString(),
                type = Mockito.isNull(),
                gender = anyString()
            )
        ).thenThrow(http)
        whenever(mapper.toError(http)).thenReturn(expected)

        val result = source.getCharacters(
            page = 9, name = "x", status = "dead", species = HUMAN, type = null, gender = "male"
        )

        assertTrue(result is DataResult.Error)
        val err = (result as DataResult.Error).error
        assertTrue(err is AppError.Http)
        assertEquals(404, (err as AppError.Http).code)
        verify(mapper, times(1)).toError(http)
    }

    @Test
    fun `getCharacterById returns Success and maps dto`() = runTest {
        val dto = CharacterDtoMother.mockRick()
        val model = CharacterModelMother.mockRick()

        whenever(api.getCharacterById(1)).thenReturn(dto)
        whenever(mapper.toModel(dto)).thenReturn(model)

        val result = source.getCharacterById(1)

        assertTrue(result is DataResult.Success)
        assertEquals(model, (result as DataResult.Success).data)
        verify(api, times(1)).getCharacterById(1)
        verify(mapper, times(1)).toModel(dto)
    }

    @Test
    fun `getCharacterById returns Error using mapper toError`() = runTest {
        val boom = IllegalStateException("boom")
        val expected = AppError.Unexpected(boom)

        whenever(api.getCharacterById(42)).thenThrow(boom)
        whenever(mapper.toError(boom)).thenReturn(expected)

        val result = source.getCharacterById(42)

        assertTrue(result is DataResult.Error)
        assertEquals(expected, (result as DataResult.Error).error)
        verify(api, times(1)).getCharacterById(42)
        verify(mapper, times(1)).toError(boom)
    }

    companion object {
        private const val NAME_RICK = "rick"
        private const val STATUS_ALIVE = "alive"
        private const val HUMAN = "human"
        private const val GENDER_MALE = "male"
        private const val PAGE_1 = 1
    }
}