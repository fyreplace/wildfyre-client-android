package app.fyreplace.client.data.repositories

import retrofit2.HttpException
import retrofit2.Response

fun <T> Response<T>.throwIfFailed() {
    if (!isSuccessful) {
        throw HttpException(this)
    }
}
