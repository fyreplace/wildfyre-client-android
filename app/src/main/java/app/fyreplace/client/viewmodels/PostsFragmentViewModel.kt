package app.fyreplace.client.viewmodels

import app.fyreplace.client.data.models.Post
import app.fyreplace.client.data.repositories.PostRepository

abstract class PostsFragmentViewModel : ItemsListFragmentViewModel<Post>() {
    open suspend fun delete(id: Long) = PostRepository.deletePost(id)
}
