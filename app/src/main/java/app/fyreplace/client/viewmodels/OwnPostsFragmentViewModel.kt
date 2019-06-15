package app.fyreplace.client.viewmodels

import android.app.Application
import app.fyreplace.client.data.models.Post
import app.fyreplace.client.data.sources.ItemsDataSourceFactory
import app.fyreplace.client.data.sources.OwnPostsDataSourceFactory

class OwnPostsFragmentViewModel(application: Application) : ItemsListFragmentViewModel<Post>(application) {
    override val factory: ItemsDataSourceFactory<Post> = OwnPostsDataSourceFactory(this, this)
}
