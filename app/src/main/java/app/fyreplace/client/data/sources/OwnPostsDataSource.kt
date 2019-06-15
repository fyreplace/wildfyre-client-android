package app.fyreplace.client.data.sources

import app.fyreplace.client.data.FailureHandler
import app.fyreplace.client.data.models.Post
import app.fyreplace.client.data.repositories.PostRepository

class OwnPostsDataSource(failureHandler: FailureHandler, listener: DataLoadingListener) :
    ItemsDataSource<Post>(failureHandler, listener) {
    override val fetcher = PostRepository::getOwnPosts
}
