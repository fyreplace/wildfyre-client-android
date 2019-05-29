package net.wildfyre.client.data.repositories

import net.wildfyre.client.R
import net.wildfyre.client.data.*

object OwnPostRepository {
    fun getPostsSync(fh: FailureHandler, offset: Int, size: Int): SuperPost? = try {
        Services.webService.getOwnPosts(
            AuthRepository.authToken.value!!,
            AreaRepository.preferredAreaName.value.orEmpty(),
            size,
            offset
        ).execute().toResult()
    } catch (e: Exception) {
        fh.onFailure(Failure(R.string.failure_request, e))
        null
    }
}

