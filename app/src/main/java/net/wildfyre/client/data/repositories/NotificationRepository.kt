package net.wildfyre.client.data.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import net.wildfyre.client.data.Services
import net.wildfyre.client.data.SuperNotification
import net.wildfyre.client.data.await

object NotificationRepository {
    private val mutableSuperNotification = MutableLiveData<SuperNotification>()

    val superNotification: LiveData<SuperNotification> = mutableSuperNotification

    suspend fun getNotifications(offset: Int, size: Int) =
        Services.webService.getNotifications(
            AuthRepository.authToken.value!!,
            size,
            offset
        ).await().also { mutableSuperNotification.postValue(it) }

    suspend fun fetchSuperNotification() = mutableSuperNotification.postValue(
        Services.webService.getNotifications(
            AuthRepository.authToken.value!!,
            1,
            0
        ).await()
    )

    suspend fun clearNotifications() = mutableSuperNotification.postValue(
        Services.webService.deleteNotifications(AuthRepository.authToken.value!!).await().let {
            SuperNotification(
                count = 0,
                results = emptyList()
            )
        }
    )
}
