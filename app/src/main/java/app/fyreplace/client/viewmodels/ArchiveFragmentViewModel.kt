package app.fyreplace.client.viewmodels

import app.fyreplace.client.data.models.Post
import app.fyreplace.client.data.sources.ArchiveDataSourceFactory

class ArchiveFragmentViewModel : ItemsListFragmentViewModel<Post>() {
    override val factory = ArchiveDataSourceFactory(this)
}
