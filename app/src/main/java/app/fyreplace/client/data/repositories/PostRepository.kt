package app.fyreplace.client.data.repositories

import app.fyreplace.client.data.Services
import app.fyreplace.client.data.await
import app.fyreplace.client.data.models.Spread
import app.fyreplace.client.data.models.Subscription

object PostRepository {
    suspend fun getArchive(offset: Int, size: Int) =
        Services.webService.getPosts(
            AuthRepository.authToken,
            AreaRepository.preferredAreaName,
            size,
            offset
        ).await()

    suspend fun getOwnPosts(offset: Int, size: Int) =
        Services.webService.getOwnPosts(
            AuthRepository.authToken,
            AreaRepository.preferredAreaName,
            size,
            offset
        ).await()

    suspend fun getNextPosts(limit: Int) =
        Services.webService.getNextPosts(
            AuthRepository.authToken,
            AreaRepository.preferredAreaName,
            limit
        ).await()

    suspend fun getPost(areaName: String, id: Long) =
        Services.webService.getPost(
            AuthRepository.authToken,
            areaName,
            id
        ).await()

    suspend fun setSubscription(areaName: String, id: Long, sub: Boolean) =
        Services.webService.putSubscription(
            AuthRepository.authToken,
            areaName,
            id,
            Subscription(sub)
        ).await()

    suspend fun spread(areaName: String, id: Long, spread: Boolean) =
        Services.webService.postSpread(
            AuthRepository.authToken,
            areaName,
            id,
            Spread(spread)
        ).await()
}
