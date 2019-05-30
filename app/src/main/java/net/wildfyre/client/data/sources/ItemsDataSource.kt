package net.wildfyre.client.data.sources

import androidx.paging.PositionalDataSource
import net.wildfyre.client.data.FailureHandler
import net.wildfyre.client.data.SuperItem

abstract class ItemsDataSource<I>(
    private val failureHandler: FailureHandler,
    private val listener: DataLoadingListener
) : PositionalDataSource<I>() {
    abstract val fetcher: (FailureHandler, Int, Int) -> SuperItem<I>?

    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<I>) {
        listener.onLoadingStart()
        val count = fetcher(failureHandler, 0, 1)?.count ?: 0
        val initialPosition = computeInitialLoadPosition(params, count)
        val initialSize = computeInitialLoadSize(params, initialPosition, count)
        callback.onResult(loadRange(initialPosition, initialSize), initialPosition, count)
        listener.onLoadingStop()
    }

    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<I>) {
        listener.onLoadingStart()
        callback.onResult(loadRange(params.startPosition, params.loadSize))
        listener.onLoadingStop()
    }

    private fun loadRange(position: Int, size: Int): List<I> =
        fetcher(failureHandler, position, size)?.results ?: emptyList()
}
