package app.fyreplace.client.viewmodels

import app.fyreplace.client.data.models.Post
import app.fyreplace.client.data.repositories.DraftRepository
import app.fyreplace.client.data.sources.DraftsDataSourceFactory

class DraftsFragmentViewModel : ItemsListFragmentViewModel<Post>() {
    override val factory = DraftsDataSourceFactory(this)

    suspend fun createDraft(anonymous: Boolean) =
        DraftRepository.createDraft(anonymous)
}
