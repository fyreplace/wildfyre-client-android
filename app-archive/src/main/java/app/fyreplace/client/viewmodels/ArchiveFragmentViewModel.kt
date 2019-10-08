package app.fyreplace.client.viewmodels

import android.content.Context
import app.fyreplace.client.data.repositories.PostRepository
import app.fyreplace.client.data.sources.ArchiveDataSourceFactory

class ArchiveFragmentViewModel(context: Context, postRepository: PostRepository) :
    PostsFragmentViewModel(context, postRepository) {
    override val factory = ArchiveDataSourceFactory(this, postRepository)
}
