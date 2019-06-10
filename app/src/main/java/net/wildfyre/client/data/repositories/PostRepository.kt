package net.wildfyre.client.data.repositories

import net.wildfyre.client.data.Services
import net.wildfyre.client.data.Spread
import net.wildfyre.client.data.Subscription
import net.wildfyre.client.data.await

object PostRepository {
    suspend fun getArchive(offset: Int, size: Int) =
        Services.webService.getPosts(
            AuthRepository.authToken.value!!,
            AreaRepository.preferredAreaName,
            size,
            offset
        ).await()

    suspend fun getOwnPosts(offset: Int, size: Int) =
        Services.webService.getOwnPosts(
            AuthRepository.authToken.value!!,
            AreaRepository.preferredAreaName,
            size,
            offset
        ).await()

    suspend fun getNextPosts(limit: Int) =
        Services.webService.getNextPosts(
            AuthRepository.authToken.value!!,
            AreaRepository.preferredAreaName,
            limit
        ).await()

    suspend fun getPost(areaName: String?, id: Long) =
        Services.webService.getPost(
            AuthRepository.authToken.value!!,
            areaName ?: AreaRepository.preferredAreaName,
            id
        ).await()

    suspend fun setSubscription(areaName: String?, id: Long, sub: Boolean) =
        Services.webService.putSubscription(
            AuthRepository.authToken.value!!,
            areaName ?: AreaRepository.preferredAreaName,
            id,
            Subscription(sub)
        ).await()

    suspend fun spread(areaName: String?, id: Long, spread: Boolean) =
        Services.webService.postSpread(
            AuthRepository.authToken.value!!,
            areaName ?: AreaRepository.preferredAreaName,
            id,
            Spread(spread)
        ).await()
}
