package net.wildfyre.client.data.sources

import net.wildfyre.client.data.FailureHandler
import net.wildfyre.client.data.Post
import net.wildfyre.client.data.repositories.PostRepository

class OwnPostsDataSource(failureHandler: FailureHandler, listener: DataLoadingListener) :
    ItemsDataSource<Post>(failureHandler, listener) {
    override val fetcher = PostRepository::getOwnPostsSync
}
