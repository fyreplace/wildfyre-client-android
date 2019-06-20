package app.fyreplace.client.data.repositories

import app.fyreplace.client.data.Services
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object NotificationRepository {
    suspend fun getNotifications(offset: Int, size: Int) = withContext(Dispatchers.IO) {
        Services.webService.getNotifications(
            AuthRepository.authToken,
            size,
            offset
        )
    }

    suspend fun getNotificationCount() = withContext(Dispatchers.IO) {
        Services.webService.getNotifications(
            AuthRepository.authToken,
            1,
            0
        ).count
    }

    suspend fun clearNotifications() = withContext(Dispatchers.IO) {
        Services.webService.deleteNotifications(AuthRepository.authToken)
    }
}
