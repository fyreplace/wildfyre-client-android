package app.fyreplace.client.data.sources

import app.fyreplace.client.data.DataLoadingListener
import app.fyreplace.client.data.models.Post
import app.fyreplace.client.data.repositories.DraftRepository

class DraftsDataSource(listener: DataLoadingListener, draftRepository: DraftRepository) :
    ItemsDataSource<Post>(listener) {
    override val fetcher = draftRepository::getDrafts
}
