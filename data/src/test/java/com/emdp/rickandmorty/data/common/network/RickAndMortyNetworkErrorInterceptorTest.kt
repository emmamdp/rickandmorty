package com.emdp.rickandmorty.data.common.network

import com.emdp.rickandmorty.data.common.network.RickAndMortyNetworkConfig.Companion.DEFAULT_BASE_URL
import com.emdp.rickandmorty.data.common.network.RickAndMortyNetworkExceptions.BadRequest
import com.emdp.rickandmorty.data.common.network.RickAndMortyNetworkExceptions.Conflict
import com.emdp.rickandmorty.data.common.network.RickAndMortyNetworkExceptions.Forbidden
import com.emdp.rickandmorty.data.common.network.RickAndMortyNetworkExceptions.NoInternet
import com.emdp.rickandmorty.data.common.network.RickAndMortyNetworkExceptions.NotFound
import com.emdp.rickandmorty.data.common.network.RickAndMortyNetworkExceptions.Serialization
import com.emdp.rickandmorty.data.common.network.RickAndMortyNetworkExceptions.ServerError
import com.emdp.rickandmorty.data.common.network.RickAndMortyNetworkExceptions.Ssl
import com.emdp.rickandmorty.data.common.network.RickAndMortyNetworkExceptions.Timeout
import com.emdp.rickandmorty.data.common.network.RickAndMortyNetworkExceptions.TooManyRequests
import com.emdp.rickandmorty.data.common.network.RickAndMortyNetworkExceptions.Unauthorized
import com.emdp.rickandmorty.data.common.network.RickAndMortyNetworkExceptions.Unknown
import com.squareup.moshi.JsonDataException
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.io.EOFException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.stream.Stream
import javax.net.ssl.SSLException
import javax.net.ssl.SSLHandshakeException

internal class RickAndMortyNetworkErrorInterceptorTest {

    private lateinit var interceptor: RickAndMortyNetworkErrorInterceptor
    private lateinit var chain: Interceptor.Chain
    private lateinit var request: Request

    @BeforeEach
    fun setUp() {
        interceptor = RickAndMortyNetworkErrorInterceptor(
            moshi = RickAndMortyNetworkProvider.defaultMoshi
        )
        chain = mock()
        request = Request.Builder().url(url = DEFAULT_BASE_URL + "character").build()

        whenever(chain.request()).thenReturn(request)
    }

    @Test
    fun shouldPassThroughOn2xx() {
        val response = successResponse()
        whenever(chain.proceed(any())).thenReturn(response)

        val out = interceptor.intercept(chain)

        assertSame(response, out)
        assertEquals(200, out.code)
        assertTrue(out.isSuccessful)
    }

    @ParameterizedTest(name = "HTTP {0} -> {4}")
    @MethodSource("httpCases")
    fun shouldMapHttpErrorToNetworkException(
        code: Int,
        body: String,
        retryAfterSeconds: Long?,
        expectedPrefix: String,
        expectedException: Class<out RickAndMortyNetworkExceptions>,
        expectedDetailContains: String?
    ) {
        val response = errorResponse(code, body, retryAfterSeconds)
        whenever(chain.proceed(any())).thenReturn(response)

        val e = assertThrows(expectedException) { interceptor.intercept(chain) }
        val msg = e.message.orEmpty()

        assertTrue(msg.contains(expectedPrefix, ignoreCase = true))
        expectedDetailContains?.let { assertTrue(msg.contains(it, ignoreCase = true)) }

        when (e) {
            is TooManyRequests -> assertEquals(retryAfterSeconds, e.retryAfterSeconds)
            is ServerError -> assertEquals(code, e.code)
            is Unknown -> assertEquals(code, e.code)
            else -> Unit
        }
    }

    @ParameterizedTest(name = "{index} -> {0} maps to {1}")
    @MethodSource("throwableCases")
    fun shouldMapThrowablesToNetworkExceptions(
        throwable: Throwable,
        expectedException: Class<out RickAndMortyNetworkExceptions>,
        expectedMessageSnippet: String?
    ) {
        whenever(chain.proceed(any())).thenThrow(throwable)

        val e = assertThrows(expectedException) { interceptor.intercept(chain) }

        expectedMessageSnippet?.let {
            assertTrue(e.message.orEmpty().contains(it, ignoreCase = true))
        }
    }

    @Test
    fun shouldFallbackToRawBodyAndTruncateWhenNoKnownFields() {
        val longPayload = "x".repeat(600)
        val rawJson = """{"foo":"$longPayload"}"""
        val response = errorResponse(500, rawJson)
        whenever(chain.proceed(any())).thenReturn(response)

        val e = assertThrows(ServerError::class.java) { interceptor.intercept(chain) }
        val msg = e.message.orEmpty()
        val expectedTruncated = rawJson.take(512)

        assertTrue(msg.contains("Server error (500)", ignoreCase = true))
        assertTrue(msg.contains(expectedTruncated))
        assertFalse(msg.contains(rawJson))
    }

    private fun successResponse(): Response {
        val body = """{"ok":true}"""
        return baseResponseBuilder(200)
            .body(body.toResponseBody("application/json".toMediaType()))
            .message("OK")
            .build()
    }

    private fun errorResponse(
        code: Int,
        body: String,
        retryAfterSeconds: Long? = null
    ): Response {
        val builder = baseResponseBuilder(code)
            .body(body.toResponseBody("application/json".toMediaType()))
            .message("Error")
        if (retryAfterSeconds != null) {
            builder.header("Retry-After", retryAfterSeconds.toString())
        }
        return builder.build()
    }

    private fun baseResponseBuilder(code: Int): Response.Builder {
        return Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(code)
    }

    companion object {
        @JvmStatic
        fun httpCases(): Stream<Arguments> = Stream.of(
            Arguments.of(
                400,
                """{"message":"Bad stuff"}""",
                null,
                "Bad request (400)",
                BadRequest::class.java,
                "Bad stuff"
            ),
            Arguments.of(
                400,
                """{"message":"From message","error":"From error"}""",
                null,
                "Bad request (400)",
                BadRequest::class.java,
                "From error"
            ),
            Arguments.of(
                400,
                """{"message":"Only message"}""",
                null,
                "Bad request (400)",
                BadRequest::class.java,
                "Only message"
            ),
            Arguments.of(
                401,
                "Unauthorized!",
                null,
                "Unauthorized (401)",
                Unauthorized::class.java,
                "Unauthorized"
            ),
            Arguments.of(
                403,
                """{"error":"Forbidden area"}""",
                null,
                "Forbidden (403)",
                Forbidden::class.java,
                "Forbidden area"
            ),
            Arguments.of(
                404,
                """{"error":"Not Found"}""",
                null,
                "Not found (404)",
                NotFound::class.java,
                "Not Found"
            ),
            Arguments.of(
                409,
                "Conflict!",
                null,
                "Conflict (409)",
                Conflict::class.java,
                "Conflict"
            ),
            Arguments.of(
                429,
                """{"message":"Too many requests"}""",
                60L,
                "Too many requests (429)",
                TooManyRequests::class.java,
                "Too many requests"
            ),
            Arguments.of(
                500,
                "Server exploded",
                null,
                "Server error (500)",
                ServerError::class.java,
                "Server exploded"
            ),
            Arguments.of(
                503,
                """{"message":"Service unavailable"}""",
                null,
                "Server error (503)",
                ServerError::class.java,
                "Service unavailable"
            ),
            Arguments.of(
                599,
                """{"message":"Another server error"}""",
                null,
                "Server error (599)",
                ServerError::class.java,
                "Another server error"
            ),
            Arguments.of(
                418,
                "I'm a teapot",
                null,
                "Unknown network error (418)",
                Unknown::class.java,
                "I'm a teapot"
            )
        )

        @JvmStatic
        fun throwableCases(): Stream<Arguments> = Stream.of(
            Arguments.of(
                SocketTimeoutException("timeout"),
                Timeout::class.java,
                "timeout"
            ),
            Arguments.of(
                SSLHandshakeException("bad ssl"),
                Ssl::class.java,
                "SSL"
            ),
            Arguments.of(
                SSLException("ssl"),
                Ssl::class.java,
                "SSL"
            ),
            Arguments.of(
                EOFException("eof"),
                Serialization::class.java,
                "Serialization"
            ),
            Arguments.of(
                JsonDataException("json"),
                Serialization::class.java,
                "Serialization"
            ),
            Arguments.of(
                IOException("io"),
                Unknown::class.java,
                null
            ),
            Arguments.of(
                UnknownHostException("no internet"),
                NoInternet::class.java,
                "No internet"
            )
        )
    }
}