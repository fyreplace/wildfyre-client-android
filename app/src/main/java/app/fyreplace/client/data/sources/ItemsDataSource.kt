package app.fyreplace.client.data.sources

import androidx.paging.PositionalDataSource
import app.fyreplace.client.data.models.SuperItem
import kotlinx.coroutines.*

abstract class ItemsDataSource<I>(private val listener: DataLoadingListener) : PositionalDataSource<I>(),
    CoroutineScope by CoroutineScope(SupervisorJob() + Dispatchers.IO) {
    abstract val fetcher: suspend (Int, Int) -> SuperItem<I>

    final override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<I>) {
        listener.onLoadingStart()
        val count = runFetcher(0, 1)?.count ?: 0
        val initialPosition = computeInitialLoadPosition(params, count)
        val initialSize = computeInitialLoadSize(params, initialPosition, count)
        callback.onResult(loadRange(initialPosition, initialSize), initialPosition, count)
        listener.onLoadingStop()
    }

    final override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<I>) =
        callback.onResult(loadRange(params.startPosition, params.loadSize))

    final override fun invalidate() {
        super.invalidate()
        cancel()
    }

    private fun loadRange(position: Int, size: Int): List<I> = runFetcher(position, size)?.results.orEmpty()

    private fun runFetcher(offset: Int, size: Int): SuperItem<I>? =
        runBlocking { return@runBlocking runFetchImpl(offset, size) }

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
