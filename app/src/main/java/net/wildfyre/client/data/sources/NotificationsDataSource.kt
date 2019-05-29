package net.wildfyre.client.data.sources

import net.wildfyre.client.data.FailureHandler
import net.wildfyre.client.data.Notification
import net.wildfyre.client.data.repositories.NotificationRepository

class NotificationsDataSource(failureHandler: FailureHandler, listener: DataLoadingListener) :
    ItemsDataSource<Notification>(failureHandler, listener) {
    override val fetcher = NotificationRepository::getNotificationsSync
}
