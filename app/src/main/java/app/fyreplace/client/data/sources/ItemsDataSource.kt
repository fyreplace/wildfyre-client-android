package app.fyreplace.client.data.sources

import androidx.paging.PositionalDataSource
import app.fyreplace.client.data.models.SuperItem
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
