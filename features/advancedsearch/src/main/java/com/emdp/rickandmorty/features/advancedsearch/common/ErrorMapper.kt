package com.emdp.rickandmorty.features.advancedsearch.common

import com.emdp.rickandmorty.core.common.result.AppError
import com.emdp.rickandmorty.features.advancedsearch.R

object ErrorMapper {

    fun mapToUserMessage(error: AppError): Int {
        return when (error) {
            is AppError.Network -> R.string.error_network

            is AppError.Http -> when (error.code) {
                404 -> R.string.error_not_found
                in 500..599 -> R.string.error_server
                else -> R.string.error_unknown
            }

            AppError.DataNotFound -> R.string.error_not_found

            is AppError.Serialization,
            is AppError.Unexpected -> R.string.error_unknown
        }
    }
}