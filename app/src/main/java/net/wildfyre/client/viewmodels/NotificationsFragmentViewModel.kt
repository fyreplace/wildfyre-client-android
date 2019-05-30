package net.wildfyre.client.viewmodels

import android.app.Application
import net.wildfyre.client.data.Notification
import net.wildfyre.client.data.repositories.NotificationRepository
import net.wildfyre.client.data.sources.ItemsDataSourceFactory
import net.wildfyre.client.data.sources.NotificationsDataSourceFactory

class NotificationsFragmentViewModel(application: Application) : ItemsListViewModel<Notification>(application) {
    override val factory: ItemsDataSourceFactory<Notification> = NotificationsDataSourceFactory(this, this)
    private var firstLoading = true

    override fun onLoadingStart() {
        if (firstLoading) {
            super.onLoadingStart()
        }
    }

    override fun onLoadingStop() {
        if (firstLoading) {
            firstLoading = false
            super.onLoadingStop()
        }
    }

    fun clearNotifications() = NotificationRepository.clearNotifications(this)
}
