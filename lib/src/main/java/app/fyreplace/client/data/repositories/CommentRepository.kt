package app.fyreplace.client.data.repositories

import app.fyreplace.client.data.models.CommentText
import app.fyreplace.client.data.models.ImageData
import app.fyreplace.client.data.services.WildFyreService
import app.fyreplace.client.data.services.createFormData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody.Part.createFormData

class CommentRepository(private val wildFyre: WildFyreService, private val areas: AreaRepository) {
    suspend fun sendComment(areaName: String?, postId: Long, comment: String, image: ImageData?) =
        withContext(Dispatchers.IO) {
            val area = areaName ?: areas.preferredAreaName

            if (image == null) {
                wildFyre.postComment(area, postId, CommentText(comment))
            } else {
                wildFyre.postComment(
                    area,
                    postId,
                    createFormData("image", image),
                    createFormData("text", comment)
                )
            }
        }

    suspend fun deleteComment(areaName: String?, postId: Long, commentId: Long) =
        withContext(Dispatchers.IO) {
            wildFyre.deleteComment(areaName ?: areas.preferredAreaName, postId, commentId)
                .throwIfFailed()
        }
}
