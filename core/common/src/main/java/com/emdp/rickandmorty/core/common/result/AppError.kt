package com.emdp.rickandmorty.core.common.result

sealed class AppError : Throwable() {
    data class Network(override val cause: Throwable? = null) : AppError()
    data class Http(val code: Int, override val message: String? = null) : AppError()
    data class Serialization(override val cause: Throwable? = null) : AppError()
    data class Unexpected(override val cause: Throwable? = null) : AppError()
    data object DataNotFound : AppError()
    data object NoResultsFound : AppError()
}
