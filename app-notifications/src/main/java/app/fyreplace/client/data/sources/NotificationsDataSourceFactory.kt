package app.fyreplace.client.data.sources

import app.fyreplace.client.data.DataLoadingListener
import app.fyreplace.client.data.models.Notification
import app.fyreplace.client.data.repositories.NotificationRepository

class NotificationsDataSourceFactory(
    private val listener: DataLoadingListener,
    private val notificationRepository: NotificationRepository
) : ItemsDataSourceFactory<Notification>() {
    override fun newSource() = NotificationsDataSource(listener, notificationRepository)
}
