package app.fyreplace.client.viewmodels

import app.fyreplace.client.data.models.Post
import app.fyreplace.client.data.sources.ArchiveDataSourceFactory
import app.fyreplace.client.data.sources.ItemsDataSourceFactory

class ArchiveFragmentViewModel : ItemsListFragmentViewModel<Post>() {
    override val factory: ItemsDataSourceFactory<Post> = ArchiveDataSourceFactory(this)
}
