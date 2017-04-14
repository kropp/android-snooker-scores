package org.snooker.api

import kotlinx.coroutines.experimental.suspendCancellableCoroutine
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


suspend fun <T> Call<T>.await(): T = suspendCancellableCoroutine { continuation ->
    enqueue(object : Callback<T> {
        override fun onResponse(call: Call<T>, response: Response<T>) {
            continuation.resume(response.body())
        }
        override fun onFailure(call: Call<T>, t: Throwable) {
            continuation.resumeWithException(t)
        }
    })

    continuation.invokeOnCompletion {
        if (continuation.isCancelled) {
            try {
                cancel()
            } catch (ex: Throwable) {
            }
        }
    }
}