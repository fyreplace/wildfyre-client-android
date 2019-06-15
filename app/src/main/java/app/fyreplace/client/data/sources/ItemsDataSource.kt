package app.fyreplace.client.data.sources

import androidx.paging.PositionalDataSource
import app.fyreplace.client.data.FailureHandler
import app.fyreplace.client.data.models.SuperItem
import kotlinx.coroutines.*

abstract class ItemsDataSource<I>(
    private val failureHandler: FailureHandler,
    private val listener: DataLoadingListener
) : PositionalDataSource<I>(), CoroutineScope by CoroutineScope(SupervisorJob() + Dispatchers.IO) {
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

    private fun runFetcher(offset: Int, size: Int): SuperItem<I>? = runBlocking {
        var success = false

        do {
            try {
                return@runBlocking fetcher(offset, size).also { success = true }
            } catch (e: Exception) {
                failureHandler.onFailure(e)
                delay(1000)
            }
        } while (!success)

        return@runBlocking null // This line should be impossible to reach
    }
}
