package net.wildfyre.client.viewmodels

import android.app.Application
import net.wildfyre.client.data.Notification
import net.wildfyre.client.data.repositories.NotificationRepository
import net.wildfyre.client.data.sources.ItemsDataSourceFactory
import net.wildfyre.client.data.sources.NotificationsDataSourceFactory

class NotificationsFragmentViewModel(application: Application) : ItemsListViewModel<Notification>(application) {
    override val factory: ItemsDataSourceFactory<Notification> = NotificationsDataSourceFactory(this, this)

    fun clearNotificationsAsync() = launchCatching { NotificationRepository.clearNotifications() }
}
