package net.wildfyre.client.data

import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@JvmName("awaitResult")
suspend fun <T> Call<T>.await() = suspendCancellableCoroutine<T> { continuation ->
    awaitImpl(continuation) { call, result ->
        result?.let { continuation.resume(it) } ?: onFailure(call, ApiNoResultException())
    }
}

@JvmName("awaitNothing")
suspend fun Call<Unit>.await() = suspendCancellableCoroutine<Unit> { continuation ->
    awaitImpl(continuation) { _, _ -> continuation.resume(Unit) }
}

private fun <T> Call<T>.awaitImpl(
    continuation: CancellableContinuation<T>,
    callback: Callback<T>.(Call<T>, T?) -> Unit
) {
    continuation.invokeOnCancellation { cancel() }
    enqueue(object : Callback<T> {
        override fun onResponse(call: Call<T>, response: Response<T>) {
            if (response.isSuccessful) {
                callback(call, response.body())
            } else {
                val body = response.errorBody()?.use { it.charStream().readText() }.orEmpty()
                onFailure(call, ApiCallException(response.code(), response.message(), body))
            }
        }

        override fun onFailure(call: Call<T>, t: Throwable) = continuation.resumeWithException(t)
    })
}

class ApiCallException(code: Int, message: String, body: String) : Exception("$code: $message\n\t$body")

class ApiNoResultException : Exception("No body result received")
