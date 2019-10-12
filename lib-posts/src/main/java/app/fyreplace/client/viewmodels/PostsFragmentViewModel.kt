package app.fyreplace.client.viewmodels

import android.content.res.Resources
import app.fyreplace.client.data.models.Post
import app.fyreplace.client.data.repositories.PostRepository

abstract class PostsFragmentViewModel(
    resources: Resources,
    private val postRepository: PostRepository
) : ItemsListFragmentViewModel<Post>(resources) {
    open suspend fun delete(id: Long) = postRepository.deletePost(null, id)
}
