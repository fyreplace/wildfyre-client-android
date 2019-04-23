package net.wildfyre.client.viewmodels

import android.app.Application
import net.wildfyre.client.data.NotificationRepository

class NotificationsFragmentViewModel(application: Application) : FailureHandlingViewModel(application) {
    fun updateNotifications() {
        NotificationRepository.fetchSuperNotification(this, 10, 0)
    }
}