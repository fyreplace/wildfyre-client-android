package app.fyreplace.client.data.sources

import app.fyreplace.client.data.FailureHandler
import app.fyreplace.client.data.models.Notification
import app.fyreplace.client.data.repositories.NotificationRepository

class NotificationsDataSource(failureHandler: FailureHandler, listener: DataLoadingListener) :
    ItemsDataSource<Notification>(failureHandler, listener) {
    override val fetcher = NotificationRepository::getNotifications
}
