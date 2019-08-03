package app.fyreplace.client.data.sources

import androidx.paging.PositionalDataSource
import app.fyreplace.client.data.models.Notification
import app.fyreplace.client.data.models.Post
import app.fyreplace.client.data.models.SuperItem
import app.fyreplace.client.data.repositories.DraftRepository
import app.fyreplace.client.data.repositories.NotificationRepository
import app.fyreplace.client.data.repositories.PostRepository
import kotlinx.coroutines.*

abstract class ItemsDataSource<I>(private val listener: DataLoadingListener) : PositionalDataSource<I>(),
    CoroutineScope by CoroutineScope(SupervisorJob() + Dispatchers.IO) {
    abstract val fetcher: suspend (Int, Int) -> SuperItem<I>

    final override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<I>) {
        listener.onLoadingStart()
        val count = runFetcher(0, 1).count
        val initialPosition = computeInitialLoadPosition(params, count)
        val initialSize = computeInitialLoadSize(params, initialPosition, count)
        val fetch = runFetcher(initialPosition, initialSize)
        callback.onResult(fetch.results, initialPosition, fetch.count)
        listener.onLoadingStop()
    }

    final override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<I>) =
        callback.onResult(runFetcher(params.startPosition, params.loadSize).results)

    final override fun invalidate() = cancel().also { super.invalidate() }

    private fun runFetcher(offset: Int, size: Int) = runBlocking { runFetchImpl(offset, size) }

    private suspend fun runFetchImpl(offset: Int, size: Int): SuperItem<I> {
        while (true) {
            try {
                return fetcher(offset, size)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                delay(FETCH_DELAY)
            }
        }
    }

    private companion object {
        const val FETCH_DELAY = 1000L
    }
}

class NotificationsDataSource(listener: DataLoadingListener) :
    ItemsDataSource<Notification>(listener) {
    override val fetcher = NotificationRepository::getNotifications
}

class ArchiveDataSource(listener: DataLoadingListener) : ItemsDataSource<Post>(listener) {
    override val fetcher = PostRepository::getArchive
}

class OwnPostsDataSource(listener: DataLoadingListener) : ItemsDataSource<Post>(listener) {
    override val fetcher = PostRepository::getOwnPosts
}

class DraftsDataSource(listener: DataLoadingListener) : ItemsDataSource<Post>(listener) {
    override val fetcher = DraftRepository::getDrafts
}
