package app.fyreplace.client.viewmodels

import android.content.res.Resources
import app.fyreplace.client.data.repositories.DraftRepository
import app.fyreplace.client.data.repositories.PostRepository
import app.fyreplace.client.data.sources.DraftsDataSourceFactory
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.parameter.parametersOf

class DraftsFragmentViewModel(
    resources: Resources,
    postRepository: PostRepository,
    private val draftRepository: DraftRepository
) : PostsFragmentViewModel(resources, postRepository), KoinComponent {
    override val factory by inject<DraftsDataSourceFactory> { parametersOf(this) }

    override suspend fun delete(id: Long) = draftRepository.deleteDraft(id)

    suspend fun createDraft() = draftRepository.createDraft(null)
}
