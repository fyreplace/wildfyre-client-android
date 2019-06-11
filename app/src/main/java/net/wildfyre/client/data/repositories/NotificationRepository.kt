package net.wildfyre.client.data.repositories

import net.wildfyre.client.data.Services
import net.wildfyre.client.data.await

object NotificationRepository {
    suspend fun getNotifications(offset: Int, size: Int) =
        Services.webService.getNotifications(
            AuthRepository.authToken,
            size,
            offset
        ).await()

    suspend fun getNotificationCount() =
        Services.webService.getNotifications(
            AuthRepository.authToken,
            1,
            0
        ).await().count

    suspend fun clearNotifications() = Services.webService.deleteNotifications(AuthRepository.authToken).await()
}
