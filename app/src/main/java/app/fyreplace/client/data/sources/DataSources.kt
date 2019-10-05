package app.fyreplace.client.data.sources

import app.fyreplace.client.data.DataLoadingListener
import app.fyreplace.client.data.models.Post
import app.fyreplace.client.data.repositories.DraftRepository
import app.fyreplace.client.data.repositories.PostRepository

class ArchiveDataSource(listener: DataLoadingListener, postRepository: PostRepository) :
    ItemsDataSource<Post>(listener) {
    override val fetcher = postRepository::getArchive
}

class OwnPostsDataSource(listener: DataLoadingListener, postRepository: PostRepository) :
    ItemsDataSource<Post>(listener) {
    override val fetcher = postRepository::getOwnPosts
}

class DraftsDataSource(listener: DataLoadingListener, draftRepository: DraftRepository) :
    ItemsDataSource<Post>(listener) {
    override val fetcher = draftRepository::getDrafts
}
