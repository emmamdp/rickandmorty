package com.emdp.rickandmorty.data.common.network

import java.io.IOException

sealed class RickAndMortyNetworkExceptions(
    message: String? = null,
    cause: Throwable? = null
) : IOException(message, cause) {

    class NoInternet(
        cause: Throwable? = null
    ) : RickAndMortyNetworkExceptions(message = "No internet connection", cause)

    class Timeout(
        cause: Throwable? = null
    ) : RickAndMortyNetworkExceptions(message = "Network timeout", cause)

    class Ssl(
        cause: Throwable? = null
    ) : RickAndMortyNetworkExceptions(message = "SSL error", cause)

    class Serialization(
        message: String? = null,
        cause: Throwable? = null
    ) : RickAndMortyNetworkExceptions(message = message ?: "Serialization error", cause)

    class BadRequest(
        message: String? = null
    ) : RickAndMortyNetworkExceptions(message = message ?: "Bad request (400)")

    class Unauthorized(
        message: String? = null
    ) : RickAndMortyNetworkExceptions(message = message ?: "Unauthorized (401)")

    class Forbidden(
        message: String? = null
    ) : RickAndMortyNetworkExceptions(message = message ?: "Forbidden (403)")

    class NotFound(
        message: String? = null
    ) : RickAndMortyNetworkExceptions(message = message ?: "Not found (404)")

    class Conflict(
        message: String? = null
    ) : RickAndMortyNetworkExceptions(message = message ?: "Conflict (409)")

    class TooManyRequests(
        val retryAfterSeconds: Long? = null,
        message: String? = null
    ) : RickAndMortyNetworkExceptions(message = message ?: "Too many requests (429)")

    class ServerError(
        val code: Int,
        message: String? = null
    ) : RickAndMortyNetworkExceptions(message = message ?: "Server error ($code)")

    class Unknown(
        val code: Int?,
        message: String? = null,
        cause: Throwable? = null
    ) : RickAndMortyNetworkExceptions(message = message ?: "Unknown network error", cause)
}