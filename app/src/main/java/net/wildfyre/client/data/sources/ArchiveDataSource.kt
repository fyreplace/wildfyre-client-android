package net.wildfyre.client.data.sources

import net.wildfyre.client.data.FailureHandler
import net.wildfyre.client.data.Post
import net.wildfyre.client.data.repositories.ArchiveRepository

class ArchiveDataSource(failureHandler: FailureHandler, listener: DataLoadingListener) :
    ItemsDataSource<Post>(failureHandler, listener) {
    override val fetcher = ArchiveRepository::getPostsSync
}
