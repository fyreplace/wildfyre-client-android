package net.wildfyre.client.data.repositories

import net.wildfyre.client.R
import net.wildfyre.client.data.*

object ArchiveRepository {
    fun getPostsSync(fh: FailureHandler, offset: Int, size: Int): SuperPost? = try {
        Services.webService.getPosts(
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
