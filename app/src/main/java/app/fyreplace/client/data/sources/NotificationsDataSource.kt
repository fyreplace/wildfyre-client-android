package app.fyreplace.client.data.sources

import app.fyreplace.client.data.models.Notification
import app.fyreplace.client.data.repositories.NotificationRepository

class NotificationsDataSource(listener: DataLoadingListener) :
    ItemsDataSource<Notification>(listener) {
    override val fetcher = NotificationRepository::getNotifications
}
