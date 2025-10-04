package com.emdp.rickandmorty.data.source.remote.mapper

import com.emdp.rickandmorty.core.common.result.AppError
import com.emdp.rickandmorty.data.source.remote.dto.CharacterDtoMother
import com.emdp.rickandmorty.data.source.remote.dto.CharactersResponseDtoMother
import com.emdp.rickandmorty.domain.models.enums.CharacterGender
import com.emdp.rickandmorty.domain.models.enums.CharacterStatus
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonEncodingException
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

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
    fun `toModel(response) maps pagination and results`() {
        val page = mapper.toModel(response = CharactersResponseDtoMother.mock())

        assertEquals(826, page.count)
        assertEquals(42, page.pages)
        assertEquals(2, page.nextPage)
        assertEquals(1, page.prevPage)
        assertEquals(2, page.results.size)
        assertEquals("Rick", page.results[0].name)
        assertEquals("Morty", page.results[1].name)
        assertEquals(CharacterStatus.ALIVE, page.results[0].status)
        assertEquals(CharacterStatus.DEAD, page.results[1].status)
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
        val model = CharactersRemoteMapperImpl().toModel(
            dto = CharacterDtoMother.mockStatus(apiValue)
        )
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
        val model = CharactersRemoteMapperImpl().toModel(
            dto = CharacterDtoMother.mockGender(apiValue)
        )
        assertEquals(CharacterGender.valueOf(expected), model.gender)
    }

    @Test
    fun `returns null on null or blank url`() {
        val urlNull = null
        val urlEmpty = ""
        val urlBlank = "   "

        assertNull(nextFrom(url = urlNull))
        assertNull(nextFrom(url = ""))
        assertNull(nextFrom(url = "   "))

        assertNull(prevFrom(url = urlNull))
        assertNull(prevFrom(url = urlEmpty))
        assertNull(prevFrom(url = urlBlank))
    }

    @Test
    fun `returns null when no query or empty query`() {
        val urlNoQuery = "https://api/character"
        val urlEmptyQuery = "https://api/character?"

        assertNull(nextFrom(url = urlNoQuery))
        assertNull(nextFrom(url = urlEmptyQuery))

        assertNull(prevFrom(url = urlNoQuery))
        assertNull(prevFrom(url = urlEmptyQuery))
    }

    @Test
    fun `returns null when page param is missing or malformed`() {
        val urlNoPage = "https://api/character?name=rick&status=alive"
        val urlEmptyPage = "https://api/character?page="
        val urlNonNumericPage = "https://api/character?page=abc"
        val urlWrongCasePage = "https://api/character?PAGE=5"

        assertNull(nextFrom(url = urlNoPage))
        assertNull(nextFrom(url = urlEmptyPage))
        assertNull(nextFrom(url = urlNonNumericPage))
        assertNull(nextFrom(url = urlWrongCasePage))

        assertNull(prevFrom(url = urlNoPage))
        assertNull(prevFrom(url = urlEmptyPage))
        assertNull(prevFrom(url = urlNonNumericPage))
        assertNull(prevFrom(url = urlWrongCasePage))
    }

    @Test
    fun `parses page when present in different positions`() {
        val urlWithPage = "https://api/character?page=2"
        val urlWithQueryNameAndPage = "https://api/character?page=7&name=rick"
        val urlWithQueryPageAndStatus = "https://api/character?name=rick&page=3&status=alive"
        val urlWithQueryStatusAndPage = "https://api/character?status=alive&page=10"

        assertEquals(2, nextFrom(url = urlWithPage))
        assertEquals(3, nextFrom(url = urlWithQueryPageAndStatus))
        assertEquals(10, nextFrom(url = urlWithQueryStatusAndPage))
        assertEquals(7, nextFrom(url = urlWithQueryNameAndPage))

        assertEquals(2, prevFrom(url = urlWithPage))
        assertEquals(3, prevFrom(url = urlWithQueryPageAndStatus))
        assertEquals(10, prevFrom(url = urlWithQueryStatusAndPage))
        assertEquals(7, prevFrom(url = urlWithQueryNameAndPage))
    }

    @Test
    fun `toError maps HttpException to AppError_Http with correct code`() {
        val errorBody = """{"error":"Not found"}""".toResponseBody("application/json".toMediaType())
        val http = HttpException(Response.error<Any>(404, errorBody))

        val error = mapper.toError(http)

        assertTrue(error is AppError.Http)
        val httpErr = error as AppError.Http
        assertEquals(404, httpErr.code)
    }

    @Test
    fun `toError maps JsonDataException to AppError_Serialization`() {
        val error = mapper.toError(JsonDataException("bad json"))
        assertTrue(error is AppError.Serialization)
    }

    @Test
    fun `toError maps JsonEncodingException to AppError_Serialization`() {
        val error = mapper.toError(JsonEncodingException("encoding issue"))
        assertTrue(error is AppError.Serialization)
    }

    @Test
    fun `toError maps IOException to AppError_Network`() {
        val error = mapper.toError(IOException("timeout"))
        assertTrue(error is AppError.Network)
    }

    @Test
    fun `toError maps unexpected Throwable to AppError_Unexpected`() {
        val error = mapper.toError(IllegalStateException("boom"))
        assertTrue(error is AppError.Unexpected)
    }

    private fun nextFrom(url: String?): Int? {
        val resp = CharactersResponseDtoMother.mockInfoNextUrl(next = url)
        return mapper.toModel(resp).nextPage
    }

    private fun prevFrom(url: String?): Int? {
        val resp = CharactersResponseDtoMother.mockInfoPrevUrl(prev = url)
        return mapper.toModel(resp).prevPage
    }
}