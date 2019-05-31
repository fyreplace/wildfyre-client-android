package net.wildfyre.client.data.repositories

import net.wildfyre.client.data.*

object CommentRepository {
    fun sendComment(fh: FailureHandler, areaName: String?, postId: Long, comment: String, callback: (Comment) -> Unit) {
        Services.webService.postComment(
            AuthRepository.authToken.value!!,
            areaName ?: AreaRepository.preferredAreaName.value.orEmpty(),
            postId,
            CommentText(comment)
        ).then(fh, callback)
    }

    fun deleteComment(fh: FailureHandler, areaName: String?, postId: Long, commentId: Long, callback: () -> Unit) {
        Services.webService.deleteComment(
            AuthRepository.authToken.value!!,
            areaName ?: AreaRepository.preferredAreaName.value.orEmpty(),
            postId,
            commentId
        ).then(fh, callback)
    }
}
