package com.example.starkfuturetest.core.result

sealed interface AppError {
    data object EmptyData : AppError
    data object ParseError : AppError
    data object AssetReadError : AppError
    data class Unknown(val cause: Throwable? = null) : AppError
}