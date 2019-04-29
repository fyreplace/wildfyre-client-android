package net.wildfyre.client.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import net.wildfyre.client.data.Notification
import net.wildfyre.client.data.NotificationRepository
import net.wildfyre.client.data.SuperNotification

class NotificationsFragmentViewModel(application: Application) : FailureHandlingViewModel(application) {
    private val _notifications = MediatorLiveData<List<Notification>>()

    val superNotification: LiveData<SuperNotification> = NotificationRepository.superNotification
    val notificationCount: LiveData<Long> =
        Transformations.map(superNotification) { it.count ?: 0 }
    val notifications: LiveData<List<Notification>> = _notifications

    init {
        _notifications.value = listOf()
        _notifications.addSource(superNotification) {
            _notifications.value = if (it.count == 0L) listOf() else _notifications.value!! + it.results!!
        }
    }

    fun fetchNextNotifications() = NotificationRepository.fetchNextNotifications(this)

    fun resetNotifications() = NotificationRepository.resetNotifications()

    fun clearNotifications() = NotificationRepository.clearNotifications(this)
}