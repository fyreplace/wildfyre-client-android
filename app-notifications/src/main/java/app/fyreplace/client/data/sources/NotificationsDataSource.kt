package app.fyreplace.client.data.sources

import app.fyreplace.client.data.DataLoadingListener
import app.fyreplace.client.data.models.Notification
import app.fyreplace.client.data.repositories.NotificationRepository

class NotificationsDataSource(
    listener: DataLoadingListener,
    notificationRepository: NotificationRepository
) : ItemsDataSource<Notification>(listener) {
    override val fetcher = notificationRepository::getNotifications
}
