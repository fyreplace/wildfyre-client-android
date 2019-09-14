package app.fyreplace.client.data.repositories

import app.fyreplace.client.data.models.CommentText
import app.fyreplace.client.data.models.ImageData
import app.fyreplace.client.data.services.WildFyreService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody.Part.createFormData

class CommentRepository(private val wildFyre: WildFyreService) {
    suspend fun sendComment(areaName: String, postId: Long, comment: String, image: ImageData?) =
        withContext(Dispatchers.IO) {
            if (image == null) {
                wildFyre.postComment(areaName, postId, CommentText(comment))
            } else {
                wildFyre.postImage(
                    areaName,
                    postId,
                    createFormData("image", image),
                    createFormData("text", comment)
                )
            }
        }

    suspend fun deleteComment(areaName: String, postId: Long, commentId: Long) =
        withContext(Dispatchers.IO) {
            wildFyre.deleteComment(areaName, postId, commentId)
            return@withContext
        }
}
