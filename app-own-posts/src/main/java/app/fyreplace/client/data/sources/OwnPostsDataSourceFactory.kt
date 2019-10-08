package app.fyreplace.client.data.sources

import app.fyreplace.client.data.DataLoadingListener
import app.fyreplace.client.data.models.Post
import app.fyreplace.client.data.repositories.PostRepository

class OwnPostsDataSourceFactory(
    private val listener: DataLoadingListener,
    private val postRepository: PostRepository
) : ItemsDataSourceFactory<Post>() {
    override fun newSource() = OwnPostsDataSource(listener, postRepository)
}
