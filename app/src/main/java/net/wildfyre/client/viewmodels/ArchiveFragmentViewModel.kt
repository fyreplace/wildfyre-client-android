package net.wildfyre.client.viewmodels

import android.app.Application
import net.wildfyre.client.data.Post
import net.wildfyre.client.data.sources.ArchiveDataSourceFactory
import net.wildfyre.client.data.sources.ItemsDataSourceFactory

class ArchiveFragmentViewModel(application: Application) : ItemsListViewModel<Post>(application) {
    override val factory: ItemsDataSourceFactory<Post> = ArchiveDataSourceFactory(this, this)
}
