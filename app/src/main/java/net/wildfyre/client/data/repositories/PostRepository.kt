package net.wildfyre.client.data.repositories

import net.wildfyre.client.data.Services
import net.wildfyre.client.data.await

object PostRepository {
    suspend fun getPost(areaName: String?, id: Long) =
        Services.webService.getPost(
            AuthRepository.authToken.value!!,
            areaName ?: AreaRepository.preferredAreaName.value.orEmpty(),
            id
        ).await()

    suspend fun getArchive(offset: Int, size: Int) =
        Services.webService.getPosts(
            AuthRepository.authToken.value!!,
            AreaRepository.preferredAreaName.value.orEmpty(),
            size,
            offset
        ).await()

    suspend fun getOwnPosts(offset: Int, size: Int) =
        Services.webService.getOwnPosts(
            AuthRepository.authToken.value!!,
            AreaRepository.preferredAreaName.value.orEmpty(),
            size,
            offset
        ).await()
}
