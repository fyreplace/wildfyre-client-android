package app.fyreplace.client.data.sources

import app.fyreplace.client.data.DataLoadingListener
import app.fyreplace.client.data.models.Post
import app.fyreplace.client.data.repositories.DraftRepository

class DraftsDataSourceFactory(
    private val listener: DataLoadingListener,
    private val postRepository: DraftRepository
) : ItemsDataSourceFactory<Post>() {
    override fun newSource() = DraftsDataSource(listener, postRepository)
}
