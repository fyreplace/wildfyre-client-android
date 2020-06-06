package app.fyreplace.client.viewmodels

import android.content.res.Resources
import app.fyreplace.client.data.models.Notification
import app.fyreplace.client.data.repositories.NotificationRepository
import app.fyreplace.client.data.sources.NotificationsDataSourceFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.parameter.parametersOf

class NotificationsFragmentViewModel(
    resources: Resources,
    private val notificationRepository: NotificationRepository
) : ItemsListFragmentViewModel<Notification>(resources), KoinComponent {
    override val factory by inject<NotificationsDataSourceFactory> { parametersOf(this) }

    suspend fun clearNotifications() {
        notificationRepository.clearNotifications()
        withContext(Dispatchers.Main.immediate) { dataSource.value?.invalidate() }
    }
}
