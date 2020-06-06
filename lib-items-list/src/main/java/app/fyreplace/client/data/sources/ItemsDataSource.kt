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
    var totalSize = 0
        private set
    protected abstract val fetcher: suspend (Int, Int) -> SuperItem<I>

    final override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<I?>) {
        try {
            listener.onLoadingStart()
            val position = params.requestedStartPosition
            callback.onResult(runFetcher(position, params.requestedLoadSize).results, position)
        } catch (e: Exception) {
            callback.onResult(emptyList(), 0, 0)
        } finally {
            listener.onLoadingStop()
        }
    }

    final override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<I?>) =
        callback.onResult(
            try {
                runFetcher(params.startPosition, params.loadSize).results
            } catch (e: Exception) {
                List(params.loadSize) { null }
            }
        )

    final override fun invalidate() {
        super.invalidate()
        cancel()
    }

    private fun runFetcher(offset: Int, size: Int) =
        runBlocking(coroutineContext) { fetcher(offset, size).apply { totalSize = count } }
}
