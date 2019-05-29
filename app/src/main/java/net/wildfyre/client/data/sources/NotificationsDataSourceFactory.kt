package net.wildfyre.client.data.sources

import net.wildfyre.client.data.FailureHandler
import net.wildfyre.client.data.Notification

class NotificationsDataSourceFactory(failureHandler: FailureHandler, private val listener: DataLoadingListener) :
    ItemsDataSourceFactory<Notification>(failureHandler) {
    override fun newSource(): ItemsDataSource<Notification> = NotificationsDataSource(failureHandler, listener)
}
