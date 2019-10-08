package app.fyreplace.client.viewmodels

import android.content.Context
import app.fyreplace.client.data.repositories.DraftRepository
import app.fyreplace.client.data.repositories.PostRepository
import app.fyreplace.client.data.sources.DraftsDataSourceFactory

class DraftsFragmentViewModel(
    context: Context,
    postRepository: PostRepository,
    private val draftRepository: DraftRepository
) : PostsFragmentViewModel(context, postRepository) {
    override val factory = DraftsDataSourceFactory(this, draftRepository)

    override suspend fun delete(id: Long) = draftRepository.deleteDraft(id)

    suspend fun createDraft() = draftRepository.createDraft()
}
