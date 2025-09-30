package com.emdp.rickandmorty.data.common.network

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
import com.squareup.moshi.Moshi
import okhttp3.Interceptor
import okhttp3.Response
import java.io.EOFException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLException
import javax.net.ssl.SSLHandshakeException

class RickAndMortyNetworkErrorInterceptor(
    private val moshi: Moshi = RickAndMortyNetworkProvider.defaultMoshi
) : Interceptor {

    private val errorAdapter by lazy {
        moshi.adapter(RickAndMortyErrorResponse::class.java)
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = try {
            chain.proceed(request)
        } catch (t: Throwable) {
            throw mapThrowable(t)
        }

        if (response.isSuccessful) return response

        val code = response.code
        val raw = try {
            response.peekBody(MAX_ERROR_PEEK_BYTES).string()
        } catch (_: Exception) {
            null
        }

        val prefix = prefixFor(code)
        val detail = parseErrorMessage(raw)

        val message = buildMessage(prefix, detail)
        val retryAfterSeconds = response.headers["Retry-After"]?.toLongOrNull()

        throw when (code) {
            400 -> BadRequest(message)
            401 -> Unauthorized(message)
            403 -> Forbidden(message)
            404 -> NotFound(message)
            409 -> Conflict(message)
            429 -> TooManyRequests(retryAfterSeconds, message)
            in 500..599 -> ServerError(code, message)
            else -> Unknown(code, message, null)
        }
    }

    private fun prefixFor(code: Int): String = when (code) {
        400 -> "Bad request (400)"
        401 -> "Unauthorized (401)"
        403 -> "Forbidden (403)"
        404 -> "Not found (404)"
        409 -> "Conflict (409)"
        429 -> "Too many requests (429)"
        in 500..599 -> "Server error ($code)"
        else -> "Unknown network error ($code)"
    }

    private fun buildMessage(prefix: String, detail: String?): String =
        if (detail.isNullOrBlank()) prefix else "$prefix: $detail"

    private fun parseErrorMessage(raw: String?): String? {
        if (raw == null) return null
        return try {
            val model = errorAdapter.fromJson(raw)
            model?.error ?: model?.message ?: raw.take(MAX_ERROR_PREVIEW_CHARS)
        } catch (_: JsonDataException) {
            raw.take(MAX_ERROR_PREVIEW_CHARS)
        } catch (_: IOException) {
            raw.take(MAX_ERROR_PREVIEW_CHARS)
        }
    }

    private fun mapThrowable(t: Throwable): RickAndMortyNetworkExceptions = when (t) {
        is UnknownHostException -> NoInternet(t)
        is SocketTimeoutException -> Timeout(t)
        is SSLHandshakeException, is SSLException -> Ssl(t)
        is EOFException, is JsonDataException -> Serialization(cause = t)
        else -> Unknown(null, t.message, t)
    }

    companion object {
        private const val MAX_ERROR_PREVIEW_CHARS = 512
        private const val MAX_ERROR_PEEK_BYTES = 64 * 1024L
    }
}