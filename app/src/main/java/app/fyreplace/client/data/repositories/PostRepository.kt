package app.fyreplace.client.data.repositories

import app.fyreplace.client.data.Services
import app.fyreplace.client.data.models.Spread
import app.fyreplace.client.data.models.Subscription
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object PostRepository {
    suspend fun getArchive(offset: Int, size: Int) = withContext(Dispatchers.IO) {
        Services.webService.getPosts(
            AuthRepository.authToken,
            AreaRepository.preferredAreaName,
            size,
            offset
        )
    }

    suspend fun getOwnPosts(offset: Int, size: Int) = withContext(Dispatchers.IO) {
        Services.webService.getOwnPosts(
            AuthRepository.authToken,
            AreaRepository.preferredAreaName,
            size,
            offset
        )
    }

    suspend fun getNextPosts(limit: Int) = withContext(Dispatchers.IO) {
        Services.webService.getNextPosts(
            AuthRepository.authToken,
            AreaRepository.preferredAreaName,
            limit
        )
    }

    suspend fun getPost(areaName: String, id: Long) = withContext(Dispatchers.IO) {
        Services.webService.getPost(
            AuthRepository.authToken,
            areaName,
            id
        )
    }

    suspend fun setSubscription(areaName: String, id: Long, sub: Boolean) =
        withContext(Dispatchers.IO) {
            Services.webService.putSubscription(
                AuthRepository.authToken,
                areaName,
                id,
                Subscription(sub)
            )
        }

    suspend fun spread(areaName: String, id: Long, spread: Boolean) = withContext(Dispatchers.IO) {
        Services.webService.postSpread(
            AuthRepository.authToken,
            areaName,
            id,
            Spread(spread)
        )
    }
}
