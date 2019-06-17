package app.fyreplace.client.data.sources

import app.fyreplace.client.data.models.Post

class OwnPostsDataSourceFactory(private val listener: DataLoadingListener) : ItemsDataSourceFactory<Post>() {
    override fun newSource(): ItemsDataSource<Post> = OwnPostsDataSource(listener)
}
