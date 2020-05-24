package app.fyreplace.client.viewmodels

import android.content.res.Resources
import app.fyreplace.client.data.repositories.PostRepository
import app.fyreplace.client.data.sources.ArchiveDataSourceFactory
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.parameter.parametersOf

class ArchiveFragmentViewModel(resources: Resources, postRepository: PostRepository) :
    PostsListFragmentViewModel(resources, postRepository, true), KoinComponent {
    override val factory by inject<ArchiveDataSourceFactory> { parametersOf(this) }
}
