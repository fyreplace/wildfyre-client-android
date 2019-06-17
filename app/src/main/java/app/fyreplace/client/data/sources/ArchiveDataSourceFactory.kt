package app.fyreplace.client.data.sources

import app.fyreplace.client.data.models.Post

class ArchiveDataSourceFactory(private val listener: DataLoadingListener) : ItemsDataSourceFactory<Post>() {
    override fun newSource(): ItemsDataSource<Post> = ArchiveDataSource(listener)
}
