package com.plcoding.cryptotracker.core.data.networking

import com.plcoding.cryptotracker.core.domain.util.NetworkError
import com.plcoding.cryptotracker.core.domain.util.Result
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse

suspend inline fun <reified  T> responseToResult(
    response: HttpResponse
): Result<T, NetworkError> {
    return when (response.status.value) {
        in 200 .. 299 -> { //success
            try {
                Result.Success(response.body<T>())
            } catch (e: NoTransformationFoundException) {
                Result.Error(NetworkError.SERIALIZATION)
            }
        }

        408 -> { //timeout
            Result.Error(NetworkError.REQUEST_TIMEOUT)
        }

        429 -> { //too many requests
            Result.Error(NetworkError.TOO_MANY_REQUEST)
        }

        in 500 ..599 -> { //server side error
            Result.Error(NetworkError.SERVER_ERROR)
        }
        else -> Result.Error(NetworkError.UNKNOWN)
    }
}

//400 -> 499 clint side error
//500 -> 599 server side error
//200 -> 299 success