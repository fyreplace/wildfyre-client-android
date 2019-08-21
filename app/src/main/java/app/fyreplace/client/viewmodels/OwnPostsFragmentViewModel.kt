package app.fyreplace.client.viewmodels

import app.fyreplace.client.data.sources.OwnPostsDataSourceFactory

class OwnPostsFragmentViewModel : PostsFragmentViewModel() {
    override val factory = OwnPostsDataSourceFactory(this)
}
