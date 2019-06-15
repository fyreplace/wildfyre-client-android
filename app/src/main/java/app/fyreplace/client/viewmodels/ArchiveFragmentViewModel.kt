package app.fyreplace.client.viewmodels

import android.app.Application
import app.fyreplace.client.data.models.Post
import app.fyreplace.client.data.sources.ArchiveDataSourceFactory
import app.fyreplace.client.data.sources.ItemsDataSourceFactory

class ArchiveFragmentViewModel(application: Application) : ItemsListFragmentViewModel<Post>(application) {
    override val factory: ItemsDataSourceFactory<Post> = ArchiveDataSourceFactory(this, this)
}
