package app.fyreplace.client.data.repositories

import app.fyreplace.client.data.Services
import app.fyreplace.client.data.models.CommentText
import app.fyreplace.client.data.models.ImageData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody

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
                    MultipartBody.Part.createFormData(
                        "image",
                        image.fileName,
                        RequestBody.create(MediaType.parse(image.mimeType), image.bytes)
                    ),
                    MultipartBody.Part.createFormData("text", comment)
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
