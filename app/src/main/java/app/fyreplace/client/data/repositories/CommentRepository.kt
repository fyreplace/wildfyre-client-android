package app.fyreplace.client.data.repositories

import app.fyreplace.client.data.Services
import app.fyreplace.client.data.models.CommentText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object CommentRepository {
    suspend fun sendComment(areaName: String, postId: Long, comment: String) = withContext(Dispatchers.IO) {
        Services.webService.postComment(
            AuthRepository.authToken,
            areaName,
            postId,
            CommentText(comment)
        )
    }

    suspend fun deleteComment(areaName: String, postId: Long, commentId: Long) = withContext(Dispatchers.IO) {
        Services.webService.deleteComment(
            AuthRepository.authToken,
            areaName,
            postId,
            commentId
        )
    }
}
