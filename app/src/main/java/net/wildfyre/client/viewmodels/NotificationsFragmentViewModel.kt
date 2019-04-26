package net.wildfyre.client.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import net.wildfyre.client.data.NotificationRepository
import net.wildfyre.client.data.SuperNotification

class NotificationsFragmentViewModel(application: Application) : FailureHandlingViewModel(application) {
    val superNotification: LiveData<SuperNotification> = NotificationRepository.superNotification
    val notificationCount: LiveData<Long> =
        Transformations.map(superNotification) { it.count ?: 0 }

    fun fetchNotifications() {
        NotificationRepository.fetchSuperNotification(this, 0, 0)
    }

    fun clearNotifications() {
        NotificationRepository.clearNotifications(this)
    }
}