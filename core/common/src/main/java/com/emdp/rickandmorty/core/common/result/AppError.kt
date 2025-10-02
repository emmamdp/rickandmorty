package com.emdp.rickandmorty.core.common.result

sealed interface AppError {
    data class Network(val cause: Throwable? = null) : AppError
    data class Http(val code: Int, val message: String? = null) : AppError
    data class Serialization(val cause: Throwable? = null) : AppError
    data class Unexpected(val cause: Throwable? = null) : AppError
}