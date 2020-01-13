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

    final override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<I?>) {
        fun validateFetch(fetch: SuperItem<I>?) =
            fetch ?: callback.onResult(emptyList(), 0, 0).let { null }

        try {
            listener.onLoadingStart()
            val countingFetch = validateFetch(runFetcher(0, 1)) ?: return
            val count = countingFetch.count
            val initialPosition = computeInitialLoadPosition(params, count)
            val initialSize = computeInitialLoadSize(params, initialPosition, count)
            val fetch = validateFetch(runFetcher(initialPosition, initialSize)) ?: return
            val actualInitialPosition = if (initialPosition < fetch.count) initialPosition else 0
            callback.onResult(fetch.results, actualInitialPosition, fetch.count)
        } finally {
            listener.onLoadingStop()
        }
    }

    final override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<I?>) {
        val fetch = runFetcher(params.startPosition, params.loadSize)
        callback.onResult(fetch?.results ?: List(params.loadSize) { null })
    }

    final override fun invalidate() {
        super.invalidate()
        cancel()
    }

    private fun runFetcher(offset: Int, size: Int): SuperItem<I>? = try {
        runBlocking(coroutineContext) { fetcher(offset, size) }
    } catch (e: Exception) {
        null
    }
}
