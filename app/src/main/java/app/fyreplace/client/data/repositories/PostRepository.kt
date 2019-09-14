package app.fyreplace.client.data.repositories

import app.fyreplace.client.data.models.Spread
import app.fyreplace.client.data.models.Subscription
import app.fyreplace.client.data.services.WildFyreService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PostRepository(private val wildFyre: WildFyreService, private val areas: AreaRepository) {
    suspend fun getArchive(offset: Int, size: Int) = withContext(Dispatchers.IO) {
        wildFyre.getPosts(areas.preferredAreaName, size, offset)
    }

    suspend fun getOwnPosts(offset: Int, size: Int) = withContext(Dispatchers.IO) {
        wildFyre.getOwnPosts(areas.preferredAreaName, size, offset)
    }

    suspend fun getNextPosts(limit: Int) = withContext(Dispatchers.IO) {
        wildFyre.getNextPosts(areas.preferredAreaName, limit)
    }

    suspend fun getPost(areaName: String, id: Long) = withContext(Dispatchers.IO) {
        wildFyre.getPost(areaName, id)
    }

    suspend fun setSubscription(areaName: String, id: Long, sub: Boolean) =
        withContext(Dispatchers.IO) {
            wildFyre.putSubscription(areaName, id, Subscription(sub))
        }

    suspend fun spread(areaName: String, id: Long, spread: Boolean) = withContext(Dispatchers.IO) {
        wildFyre.postSpread(areaName, id, Spread(spread))
        return@withContext
    }

    suspend fun deletePost(areaName: String?, id: Long) = withContext(Dispatchers.IO) {
        wildFyre.deletePost(areaName ?: areas.preferredAreaName, id)
        return@withContext
    }
}
