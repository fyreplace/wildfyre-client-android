package net.wildfyre.client.viewmodels

import android.app.Application
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.wildfyre.client.data.models.Notification
import net.wildfyre.client.data.repositories.NotificationRepository
import net.wildfyre.client.data.sources.ItemsDataSourceFactory
import net.wildfyre.client.data.sources.NotificationsDataSourceFactory

class NotificationsFragmentViewModel(application: Application) : ItemsListFragmentViewModel<Notification>(application) {
    override val factory: ItemsDataSourceFactory<Notification> = NotificationsDataSourceFactory(this, this)

    fun clearNotificationsAsync() = launchCatching {
        withContext(Dispatchers.IO) { NotificationRepository.clearNotifications() }
        dataSource.value?.invalidate()
    }
}
