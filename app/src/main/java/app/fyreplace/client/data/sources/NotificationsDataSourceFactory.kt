package app.fyreplace.client.data.sources

import app.fyreplace.client.data.FailureHandler
import app.fyreplace.client.data.models.Notification

class NotificationsDataSourceFactory(failureHandler: FailureHandler, private val listener: DataLoadingListener) :
    ItemsDataSourceFactory<Notification>(failureHandler) {
    override fun newSource(): ItemsDataSource<Notification> = NotificationsDataSource(failureHandler, listener)
}
