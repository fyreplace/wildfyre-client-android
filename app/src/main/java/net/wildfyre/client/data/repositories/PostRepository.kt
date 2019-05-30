package net.wildfyre.client.data.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import net.wildfyre.client.R
import net.wildfyre.client.data.*

object PostRepository {
    fun getPost(fh: FailureHandler, areaName: String?, id: Long): LiveData<Post> {
        val futurePost = MutableLiveData<Post>()

        if (id >= 0) {
            Services.webService.getPost(
                AuthRepository.authToken.value!!,
                areaName ?: AreaRepository.preferredAreaName.value.orEmpty(),
                id
            ).then(fh, R.string.failure_request) { futurePost.value = it }
        }

        return futurePost
    }

    fun getArchiveSync(fh: FailureHandler, offset: Int, size: Int): SuperPost? = try {
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

    fun getOwnPostsSync(fh: FailureHandler, offset: Int, size: Int): SuperPost? = try {
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
