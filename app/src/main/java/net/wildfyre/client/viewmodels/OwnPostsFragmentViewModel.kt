package net.wildfyre.client.viewmodels

import android.app.Application
import net.wildfyre.client.data.models.Post
import net.wildfyre.client.data.sources.ItemsDataSourceFactory
import net.wildfyre.client.data.sources.OwnPostsDataSourceFactory

class OwnPostsFragmentViewModel(application: Application) : ItemsListFragmentViewModel<Post>(application) {
    override val factory: ItemsDataSourceFactory<Post> = OwnPostsDataSourceFactory(this, this)
}
