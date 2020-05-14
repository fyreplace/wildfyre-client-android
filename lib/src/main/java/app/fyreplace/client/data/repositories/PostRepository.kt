package app.fyreplace.client.data.repositories

import app.fyreplace.client.data.models.Flag
import app.fyreplace.client.data.models.Spread
import app.fyreplace.client.data.models.Subscription
import app.fyreplace.client.data.services.WildFyreService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PostRepository(private val wildFyre: WildFyreService, private val areas: AreaRepository) {
    suspend fun getNextPosts(limit: Int) = withContext(Dispatchers.IO) {
        wildFyre.getNextPosts(areas.preferredAreaName, limit)
    }

    suspend fun getArchive(offset: Int, size: Int) = withContext(Dispatchers.IO) {
        wildFyre.getSubscribedPosts(areas.preferredAreaName, size, offset)
    }

    suspend fun getOwnPosts(offset: Int, size: Int) = withContext(Dispatchers.IO) {
        wildFyre.getOwnPosts(areas.preferredAreaName, size, offset)
    }

    suspend fun getPost(areaName: String?, id: Long) = withContext(Dispatchers.IO) {
        wildFyre.getPost(areaName ?: areas.preferredAreaName, id)
    }

    suspend fun getFlagChoices() = withContext(Dispatchers.IO) {
        wildFyre.getFlagReasons()
    }

    suspend fun setSubscription(areaName: String?, id: Long, sub: Boolean) =
        withContext(Dispatchers.IO) {
            wildFyre.putSubscription(areaName ?: areas.preferredAreaName, id, Subscription(sub))
        }

    suspend fun spread(areaName: String?, id: Long, spread: Boolean) = withContext(Dispatchers.IO) {
        wildFyre.postSpread(areaName ?: areas.preferredAreaName, id, Spread(spread))
    }

    suspend fun deletePost(areaName: String?, id: Long) = withContext(Dispatchers.IO) {
        wildFyre.deletePost(areaName ?: areas.preferredAreaName, id).throwIfFailed()
    }

    suspend fun flag(areaName: String?, postId: Long, commentId: Long?, flag: Flag) =
        withContext(Dispatchers.IO) {
            val area = areaName ?: areas.preferredAreaName

            if (commentId == null) {
                wildFyre.postFlag(area, postId, flag)
            } else {
                wildFyre.postFlag(area, postId, commentId, flag)
            }
        }
}
