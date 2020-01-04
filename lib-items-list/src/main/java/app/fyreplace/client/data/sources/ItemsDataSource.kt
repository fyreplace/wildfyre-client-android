package app.fyreplace.client.data.sources

import androidx.paging.PositionalDataSource
import app.fyreplace.client.data.DataLoadingListener
import app.fyreplace.client.data.models.Model
import app.fyreplace.client.data.models.SuperItem
import kotlinx.coroutines.*

abstract class ItemsDataSource<I : Model>(private val listener: DataLoadingListener) :
    PositionalDataSource<I>(), CoroutineScope by CoroutineScope(
    SupervisorJob() + Dispatchers.IO
) {
    protected abstract val fetcher: suspend (Int, Int) -> SuperItem<I>

    final override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<I>) {
        listener.onLoadingStart()
        val count = runFetcher(0, 1).count
        val initialPosition = computeInitialLoadPosition(params, count)
        val initialSize = computeInitialLoadSize(params, initialPosition, count)
        val fetch = runFetcher(initialPosition, initialSize)
        val actualInitialPosition = if (initialPosition < fetch.count) initialPosition else 0
        callback.onResult(fetch.results, actualInitialPosition, fetch.count)
        listener.onLoadingStop()
    }

    final override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<I>) =
        callback.onResult(runFetcher(params.startPosition, params.loadSize).results)

    final override fun invalidate() {
        super.invalidate()
        cancel()
    }

    private fun runFetcher(offset: Int, size: Int): SuperItem<I> = try {
        runBlocking(coroutineContext) { fetcher(offset, size) }
    } catch (e: Exception) {
        SuperItem(0, null, null, emptyList())
    }
}
