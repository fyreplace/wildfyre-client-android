package net.wildfyre.client.data.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import net.wildfyre.client.R
import net.wildfyre.client.data.*

object NotificationRepository {
    private val mutableSuperNotification = MutableLiveData<SuperNotification>()

    val superNotification: LiveData<SuperNotification> = mutableSuperNotification

    fun getNotificationsSync(fh: FailureHandler, offset: Int, size: Int): SuperNotification? = try {
        Services.webService.getNotifications(
            AuthRepository.authToken.value!!,
            size,
            offset
        ).execute().toResult()?.also { mutableSuperNotification.postValue(it) }
    } catch (e: Exception) {
        fh.onFailure(Failure(R.string.failure_request, e))
        null
    }

    fun fetchSuperNotification(fh: FailureHandler) {
        Services.webService.getNotifications(AuthRepository.authToken.value!!, 1, 0)
            .then(fh, R.string.failure_request) { mutableSuperNotification.postValue(it) }
    }

    fun clearNotifications(fh: FailureHandler) =
        Services.webService.deleteNotifications(AuthRepository.authToken.value!!)
            .then(fh, R.string.failure_request) { mutableSuperNotification.postValue(SuperNotification()) }
}
