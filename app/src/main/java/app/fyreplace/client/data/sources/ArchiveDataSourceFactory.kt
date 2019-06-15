package app.fyreplace.client.data.sources

import app.fyreplace.client.data.FailureHandler
import app.fyreplace.client.data.models.Post

class ArchiveDataSourceFactory(failureHandler: FailureHandler, private val listener: DataLoadingListener) :
    ItemsDataSourceFactory<Post>(failureHandler) {
    override fun newSource(): ItemsDataSource<Post> = ArchiveDataSource(failureHandler, listener)
}
