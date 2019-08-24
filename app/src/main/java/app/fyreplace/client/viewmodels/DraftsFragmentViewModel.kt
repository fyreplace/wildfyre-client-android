package app.fyreplace.client.viewmodels

import app.fyreplace.client.data.repositories.DraftRepository
import app.fyreplace.client.data.sources.DraftsDataSourceFactory

class DraftsFragmentViewModel : PostsFragmentViewModel() {
    override val factory = DraftsDataSourceFactory(this)

    override suspend fun delete(id: Long) = DraftRepository.deleteDraft(id)

    suspend fun createDraft() = DraftRepository.createDraft()
}
