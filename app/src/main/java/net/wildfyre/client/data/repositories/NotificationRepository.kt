package net.wildfyre.client.data.repositories

import androidx.lifecycle.LiveData
import net.wildfyre.client.R
import net.wildfyre.client.data.*

object NotificationRepository {
    private val delegate =
        AccumulatorRepositoryDelegate<Notification>()

    val superNotification: LiveData<SuperNotification> = delegate.mutableSuperItem
    val notifications: LiveData<List<Notification>> = delegate.mutableItems

    fun fetchNextNotifications(fh: FailureHandler, forContent: Boolean) {
        val call = Services.webService.getNotifications(
            AuthRepository.authToken.value!!,
            if (forContent) AccumulatorRepositoryDelegate.BUCKET_SIZE else 1,
            delegate.offset
        )

        delegate.fetchNextItems(call, fh, forContent)
    }

    fun resetNotifications() = delegate.resetItems()

    fun removeNotification(fh: FailureHandler, id: Long) =
        notifications.value!!.firstOrNull { it.post?.id == id }?.let {
            delegate.removeItem(it)
            fetchNextNotifications(fh, false)
        }

    fun clearNotifications(fh: FailureHandler) =
        Services.webService.deleteNotifications(AuthRepository.authToken.value!!)
            .then(fh, R.string.failure_request) { resetNotifications() }
}
