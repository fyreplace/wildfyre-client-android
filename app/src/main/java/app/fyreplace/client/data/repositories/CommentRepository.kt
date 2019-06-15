package app.fyreplace.client.data.repositories

import app.fyreplace.client.data.Services
import app.fyreplace.client.data.await
import app.fyreplace.client.data.models.CommentText

object CommentRepository {
    suspend fun sendComment(areaName: String, postId: Long, comment: String) =
        Services.webService.postComment(
            AuthRepository.authToken,
            areaName,
            postId,
            CommentText(comment)
        ).await()

    suspend fun deleteComment(areaName: String, postId: Long, commentId: Long) =
        Services.webService.deleteComment(
            AuthRepository.authToken,
            areaName,
            postId,
            commentId
        ).await()
}
