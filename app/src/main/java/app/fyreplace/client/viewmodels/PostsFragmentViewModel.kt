package app.fyreplace.client.viewmodels

import android.content.Context
import app.fyreplace.client.data.models.Post
import app.fyreplace.client.data.repositories.PostRepository

abstract class PostsFragmentViewModel(
    context: Context,
    private val postRepository: PostRepository
) : ItemsListFragmentViewModel<Post>(context) {
    open suspend fun delete(id: Long) = postRepository.deletePost(null, id)
}
