package app.fyreplace.client.viewmodels

import android.content.Context
import app.fyreplace.client.data.models.Notification
import app.fyreplace.client.data.repositories.NotificationRepository
import app.fyreplace.client.data.sources.NotificationsDataSourceFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NotificationsFragmentViewModel(
    context: Context,
    private val notificationRepository: NotificationRepository
) : ItemsListFragmentViewModel<Notification>(context) {
    override val factory = NotificationsDataSourceFactory(this, notificationRepository)

    suspend fun clearNotifications() {
        notificationRepository.clearNotifications()
        withContext(Dispatchers.Main) { dataSource.value?.invalidate() }
    }
}
