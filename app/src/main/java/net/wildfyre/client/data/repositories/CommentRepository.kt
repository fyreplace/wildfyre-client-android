package net.wildfyre.client.data.repositories

import net.wildfyre.client.R
import net.wildfyre.client.data.Comment
import net.wildfyre.client.data.FailureHandler
import net.wildfyre.client.data.Services
import net.wildfyre.client.data.then

object CommentRepository {
    fun sendComment(fh: FailureHandler, areaName: String?, postId: Long, comment: String, callback: (Comment) -> Unit) {
        Services.webService.postComment(
            AuthRepository.authToken.value!!,
            areaName ?: AreaRepository.preferredAreaName.value.orEmpty(),
            postId,
            Comment().apply { text = comment }
        ).then(fh, R.string.failure_request, callback)
    }

    fun deleteComment(fh: FailureHandler, areaName: String?, postId: Long, commentId: Long, callback: () -> Unit) {
        Services.webService.deleteComment(
            AuthRepository.authToken.value!!,
            areaName ?: AreaRepository.preferredAreaName.value.orEmpty(),
            postId,
            commentId
        ).then(fh, R.string.failure_request, callback)
    }
}
