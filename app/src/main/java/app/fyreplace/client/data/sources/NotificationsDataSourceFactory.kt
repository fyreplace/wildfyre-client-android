package app.fyreplace.client.data.sources

import app.fyreplace.client.data.models.Notification

class NotificationsDataSourceFactory(private val listener: DataLoadingListener) :
    ItemsDataSourceFactory<Notification>() {
    override fun newSource(): ItemsDataSource<Notification> = NotificationsDataSource(listener)
}
