package com.emdp.rickandmorty.data.source.remote.mapper

import com.emdp.rickandmorty.core.common.result.AppError
import com.emdp.rickandmorty.data.common.network.RickAndMortyNetworkExceptions
import com.emdp.rickandmorty.data.source.remote.dto.CharacterDtoMother
import com.emdp.rickandmorty.data.source.remote.dto.CharactersResponseDtoMother
import com.emdp.rickandmorty.domain.models.enums.CharacterGender
import com.emdp.rickandmorty.domain.models.enums.CharacterStatus
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonEncodingException
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.MethodSource
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.util.stream.Stream

internal class CharactersRemoteMapperImplTest {

    private val mapper = CharactersRemoteMapperImpl()

    @Test
    fun `toModel maps all basic fields from CharacterDto`() {
        val model = mapper.toModel(dto = CharacterDtoMother.mockRickSanchez())

        assertEquals(1, model.id)
        assertEquals("Rick Sanchez", model.name)
        assertEquals(CharacterStatus.ALIVE, model.status)
        assertEquals("Human", model.species)
        assertEquals("", model.type)
        assertEquals(CharacterGender.MALE, model.gender)
        assertEquals("Earth", model.originName)
        assertEquals("Citadel of Ricks", model.locationName)
        assertEquals("https://img/rick.png", model.imageUrl)
        assertEquals(listOf("e1", "e2"), model.episodeUrls)
        assertEquals("2017-11-04T18:48:46.250Z", model.createdIso)
    }

    @Test
    fun `toModel(response) maps pagination and results with requestedPage`() {
        val response = CharactersResponseDtoMother.mock()

        val pagedData = mapper.toModel(response = response, requestedPage = 3)

        assertEquals(3, pagedData.page)
        assertEquals(2, pagedData.data.size)
        assertEquals("Rick", pagedData.data[0].name)
        assertEquals("Morty", pagedData.data[1].name)
        assertEquals(CharacterStatus.ALIVE, pagedData.data[0].status)
        assertEquals(CharacterStatus.DEAD, pagedData.data[1].status)
    }

    @ParameterizedTest
    @CsvSource(
        "https://api/page=2, true",
        ", false"
    )
    fun `toModel(response) hasMore depends on next URL`(nextUrl: String?, expectedHasMore: Boolean) {
        val response = CharactersResponseDtoMother.mockInfoNextUrl(next = nextUrl)

        val pagedData = mapper.toModel(response = response, requestedPage = 1)

        assertEquals(expectedHasMore, pagedData.hasMore)
    }

    @Test
    fun `toModel(response) maps totalPages from info pages`() {
        val response = CharactersResponseDtoMother.mock()

        val pagedData = mapper.toModel(response = response, requestedPage = 1)

        assertEquals(42, pagedData.totalPages)
    }

    @ParameterizedTest
    @CsvSource(
        "Alive,ALIVE",
        "alive,ALIVE",
        "ALIVE,ALIVE",
        "Dead,DEAD",
        "DEAD,DEAD",
        "unknown,UNKNOWN",
        "stasis,UNKNOWN",
        ",UNKNOWN"
    )
    fun `status mapping is case-insensitive and falls back to UNKNOWN`(
        apiValueInput: String?,
        expected: String
    ) {
        val apiValue = apiValueInput?.trim().orEmpty()
        val model = mapper.toModel(dto = CharacterDtoMother.mockStatus(apiValue))

        assertEquals(CharacterStatus.valueOf(expected), model.status)
    }

    @ParameterizedTest
    @CsvSource(
        "Female,FEMALE",
        "female,FEMALE",
        "FEMALE,FEMALE",
        "Male,MALE",
        "genderless,GENDERLESS",
        "UNKNOWN,UNKNOWN",
        "??,UNKNOWN",
        ",UNKNOWN"
    )
    fun `gender mapping is case-insensitive and falls back to UNKNOWN`(
        apiValueInput: String?,
        expected: String
    ) {
        val apiValue = apiValueInput?.trim().orEmpty()
        val model = mapper.toModel(dto = CharacterDtoMother.mockGender(apiValue))

        assertEquals(CharacterGender.valueOf(expected), model.gender)
    }

    @Test
    fun `toError maps HttpException to AppError_Http with correct code`() {
        val errorBody = """{"error":"Not found"}""".toResponseBody("application/json".toMediaType())
        val http = HttpException(Response.error<Any>(404, errorBody))

        val error = mapper.toError(http)

        assertTrue(error is AppError.Http)
        assertEquals(404, (error as AppError.Http).code)
    }

    @ParameterizedTest
    @MethodSource("exceptionMappingProvider")
    fun `toError maps exceptions to correct AppError type`(
        exception: Throwable,
        expectedErrorType: Class<out AppError>,
        expectedCode: Int?
    ) {
        val error = mapper.toError(exception)

        assertTrue(expectedErrorType.isInstance(error))
        if (expectedCode != null && error is AppError.Http) {
            assertEquals(expectedCode, error.code)
        }
    }

    companion object {
        @JvmStatic
        fun exceptionMappingProvider(): Stream<Arguments> = Stream.of(
            Arguments.of(RickAndMortyNetworkExceptions.BadRequest(), AppError.Http::class.java, 400),
            Arguments.of(RickAndMortyNetworkExceptions.Unauthorized(), AppError.Http::class.java, 401),
            Arguments.of(RickAndMortyNetworkExceptions.Forbidden(), AppError.Http::class.java, 403),
            Arguments.of(RickAndMortyNetworkExceptions.NotFound(), AppError.Http::class.java, 404),
            Arguments.of(RickAndMortyNetworkExceptions.Conflict(), AppError.Http::class.java, 409),
            Arguments.of(RickAndMortyNetworkExceptions.TooManyRequests(), AppError.Http::class.java, 429),
            Arguments.of(RickAndMortyNetworkExceptions.ServerError(code = 503), AppError.Http::class.java, 503),
            Arguments.of(JsonDataException("bad json"), AppError.Serialization::class.java, null),
            Arguments.of(JsonEncodingException("encoding issue"), AppError.Serialization::class.java, null),
            Arguments.of(RickAndMortyNetworkExceptions.Serialization("Invalid JSON"), AppError.Serialization::class.java, null),
            Arguments.of(IOException("timeout"), AppError.Network::class.java, null),
            Arguments.of(IllegalStateException("error"), AppError.Unexpected::class.java, null)
        )
    }
}