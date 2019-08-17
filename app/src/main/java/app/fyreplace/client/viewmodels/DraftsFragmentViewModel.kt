package app.fyreplace.client.viewmodels

import app.fyreplace.client.data.models.Post
import app.fyreplace.client.data.repositories.DraftRepository
import app.fyreplace.client.data.sources.DraftsDataSourceFactory
import app.fyreplace.client.data.sources.ItemsDataSourceFactory

class DraftsFragmentViewModel : ItemsListFragmentViewModel<Post>() {
    override val factory: ItemsDataSourceFactory<Post> = DraftsDataSourceFactory(this)

    suspend fun createDraft(areaName: String, anonym: Boolean) = DraftRepository.createDraft(areaName, anonym)
}
