package net.wildfyre.client.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import net.wildfyre.client.data.Notification
import net.wildfyre.client.data.NotificationRepository

class NotificationsFragmentViewModel(application: Application) : FailureHandlingViewModel(application) {
    val notificationCount: LiveData<Long> =
        Transformations.map(NotificationRepository.superNotification) { it.count ?: 0 }
    val notifications: LiveData<List<Notification>> = NotificationRepository.notifications

    fun fetchNextNotifications() = NotificationRepository.fetchNextNotifications(this, true)

    fun resetNotifications() = NotificationRepository.resetNotifications()

    fun clearNotifications() = NotificationRepository.clearNotifications(this)
}