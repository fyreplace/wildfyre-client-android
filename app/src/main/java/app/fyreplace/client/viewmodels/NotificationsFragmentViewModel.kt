package app.fyreplace.client.viewmodels

import app.fyreplace.client.data.models.Notification
import app.fyreplace.client.data.repositories.NotificationRepository
import app.fyreplace.client.data.sources.ItemsDataSourceFactory
import app.fyreplace.client.data.sources.NotificationsDataSourceFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NotificationsFragmentViewModel : ItemsListFragmentViewModel<Notification>() {
    override val factory: ItemsDataSourceFactory<Notification> = NotificationsDataSourceFactory(this)

    suspend fun clearNotifications() {
        NotificationRepository.clearNotifications()
        withContext(Dispatchers.Main) { dataSource.value?.invalidate() }
    }
}
