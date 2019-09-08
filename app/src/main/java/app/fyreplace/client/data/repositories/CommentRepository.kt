package app.fyreplace.client.data.repositories

import app.fyreplace.client.data.Services
import app.fyreplace.client.data.models.CommentText
import app.fyreplace.client.data.models.ImageData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody.Part.createFormData

object CommentRepository {
    suspend fun sendComment(areaName: String, postId: Long, comment: String, image: ImageData?) =
        withContext(Dispatchers.IO) {
            if (image == null) {
                Services.webService.postComment(
                    AuthRepository.authToken,
                    areaName,
                    postId,
                    CommentText(comment)
                )
            } else {
                Services.webService.postImage(
                    AuthRepository.authToken,
                    areaName,
                    postId,
                    createFormData("image", image),
                    createFormData("text", comment)
                )
            }
        }

    suspend fun deleteComment(areaName: String, postId: Long, commentId: Long) =
        withContext(Dispatchers.IO) {
            Services.webService.deleteComment(
                AuthRepository.authToken,
                areaName,
                postId,
                commentId
            )
            return@withContext
        }
}
