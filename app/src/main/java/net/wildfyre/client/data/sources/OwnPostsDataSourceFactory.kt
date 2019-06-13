package net.wildfyre.client.data.sources

import net.wildfyre.client.data.FailureHandler
import net.wildfyre.client.data.models.Post

class OwnPostsDataSourceFactory(failureHandler: FailureHandler, private val listener: DataLoadingListener) :
    ItemsDataSourceFactory<Post>(failureHandler) {
    override fun newSource(): ItemsDataSource<Post> = OwnPostsDataSource(failureHandler, listener)
}
