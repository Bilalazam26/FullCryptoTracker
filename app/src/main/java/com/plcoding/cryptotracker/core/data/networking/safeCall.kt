package com.plcoding.cryptotracker.core.data.networking

import com.plcoding.cryptotracker.core.domain.util.NetworkError
import io.ktor.client.statement.HttpResponse
import com.plcoding.cryptotracker.core.domain.util.Result
import com.plcoding.cryptotracker.crypto.data.dto.CoinsResponseDto
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.coroutines.ensureActive
import kotlinx.serialization.SerializationException
import kotlin.coroutines.coroutineContext

suspend inline fun <reified T> safeCall (
    execute: () -> HttpResponse
): Result<T, NetworkError> {

    val response = try {
        execute()
    } catch (e: UnresolvedAddressException) {
        // the client is unable to resolve the address from the backend,
        // commonly happens when no Internet connection
        return Result.Error(NetworkError.NO_INTERNET)
    } catch (e: SerializationException) {
        return Result.Error(NetworkError.SERIALIZATION)
    } catch (e: Exception) {
        coroutineContext.ensureActive() // if coroutine exception to be thrown here
        return Result.Error(NetworkError.UNKNOWN)
    }
    return responseToResult(response)
}