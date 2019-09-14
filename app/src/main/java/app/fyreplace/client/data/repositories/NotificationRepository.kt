package app.fyreplace.client.data.repositories

import app.fyreplace.client.data.services.WildFyreService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NotificationRepository(
    private val wildFyre: WildFyreService,
    private val auth: AuthRepository
) {
    suspend fun getNotifications(offset: Int, size: Int) = withContext(Dispatchers.IO) {
        wildFyre.getNotifications(auth.authToken, size, offset)
    }

    suspend fun getNotificationCount() = withContext(Dispatchers.IO) {
        wildFyre.getNotifications(auth.authToken, 1, 0).count
    }

    suspend fun clearNotifications() = withContext(Dispatchers.IO) {
        wildFyre.deleteNotifications(auth.authToken)
        return@withContext
    }
}
