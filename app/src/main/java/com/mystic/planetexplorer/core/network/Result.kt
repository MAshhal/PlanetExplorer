package com.mystic.planetexplorer.core.network

/**
 * Created: Fri 05 Dec 2025
 * Author: Muhammad Ashhal
 */

sealed interface Result<out T> {
    data object Loading: Result<Nothing>
    data class Success<T>(val data: T): Result<T>
    data class Failure(val exception: Throwable): Result<Nothing>
}

inline fun <T, R> Result<T>.map(transform: (T) -> R): Result<R> {
    return when (this) {
        is Result.Success -> Result.Success(transform(data))
        is Result.Failure -> this
        is Result.Loading -> this
    }
}

suspend inline fun <T> safeApiCall(block: suspend () -> T): Result<T> {
    return try {
        Result.Success(block())
    } catch (t: Throwable) {
        Result.Failure(t)
    }
}