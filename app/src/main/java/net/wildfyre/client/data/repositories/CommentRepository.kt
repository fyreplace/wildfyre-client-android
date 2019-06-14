package net.wildfyre.client.data.repositories

import net.wildfyre.client.data.Services
import net.wildfyre.client.data.await
import net.wildfyre.client.data.models.CommentText

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
