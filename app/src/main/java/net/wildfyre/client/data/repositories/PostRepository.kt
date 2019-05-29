package net.wildfyre.client.data.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import net.wildfyre.client.R
import net.wildfyre.client.data.FailureHandler
import net.wildfyre.client.data.Post
import net.wildfyre.client.data.Services
import net.wildfyre.client.data.then

object PostRepository {
    fun getPost(fh: FailureHandler, areaName: String?, id: Long): LiveData<Post> {
        val futurePost = MutableLiveData<Post>()

        if (id >= 0) {
            Services.webService.getPost(
                AuthRepository.authToken.value!!,
                areaName ?: AreaRepository.preferredAreaName.value.orEmpty(),
                id
            ).then(fh, R.string.failure_request) {
                futurePost.value = it
                NotificationRepository.removeNotification(fh, it.id!!)
            }
        }

        return futurePost
    }
}
