package app.fyreplace.client.viewmodels

import android.content.Context
import app.fyreplace.client.data.repositories.PostRepository
import app.fyreplace.client.data.sources.OwnPostsDataSourceFactory

class OwnPostsFragmentViewModel(context: Context, postRepository: PostRepository) :
    PostsFragmentViewModel(context, postRepository) {
    override val factory = OwnPostsDataSourceFactory(this, postRepository)
}
