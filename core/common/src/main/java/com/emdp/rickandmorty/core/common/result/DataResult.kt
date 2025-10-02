package com.emdp.rickandmorty.core.common.result

sealed class DataResult<out T> {
    data class Success<T>(val data: T) : DataResult<T>()
    data class Error(val error: AppError) : DataResult<Nothing>()
}