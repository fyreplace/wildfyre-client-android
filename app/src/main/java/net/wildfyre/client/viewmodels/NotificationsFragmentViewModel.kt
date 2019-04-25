package net.wildfyre.client.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import net.wildfyre.client.data.NotificationRepository
import net.wildfyre.client.data.SuperNotification

class NotificationsFragmentViewModel(application: Application) : FailureHandlingViewModel(application) {
    val superNotification: LiveData<SuperNotification> = NotificationRepository.superNotification
    val notificationCount: LiveData<Long> =
        Transformations.map(NotificationRepository.superNotification) { it.count ?: 0 }
    var currentOffset: Long = 0L

    fun updateNotifications() {
        NotificationRepository.fetchSuperNotification(this, DEFAULT_LIMIT, currentOffset)
    }

    fun clearNotifications() {
        NotificationRepository.clearNotifications(this)
    }

    companion object {
        const val DEFAULT_LIMIT = 12L
    }
}