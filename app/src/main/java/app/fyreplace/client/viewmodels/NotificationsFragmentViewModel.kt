package app.fyreplace.client.viewmodels

import android.app.Application
import app.fyreplace.client.data.models.Notification
import app.fyreplace.client.data.repositories.NotificationRepository
import app.fyreplace.client.data.sources.ItemsDataSourceFactory
import app.fyreplace.client.data.sources.NotificationsDataSourceFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NotificationsFragmentViewModel(application: Application) : ItemsListFragmentViewModel<Notification>(application) {
    override val factory: ItemsDataSourceFactory<Notification> = NotificationsDataSourceFactory(this, this)

    fun clearNotificationsAsync() = launchCatching {
        withContext(Dispatchers.IO) { NotificationRepository.clearNotifications() }
        dataSource.value?.invalidate()
    }
}
