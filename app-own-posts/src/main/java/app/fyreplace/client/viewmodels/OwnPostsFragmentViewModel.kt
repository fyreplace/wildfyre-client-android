package app.fyreplace.client.viewmodels

import android.content.res.Resources
import app.fyreplace.client.data.repositories.PostRepository
import app.fyreplace.client.data.sources.OwnPostsDataSourceFactory
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.parameter.parametersOf

class OwnPostsFragmentViewModel(resources: Resources, postRepository: PostRepository) :
    PostsListFragmentViewModel(resources, postRepository, false), KoinComponent {
    override val factory by inject<OwnPostsDataSourceFactory> { parametersOf(this) }
}
