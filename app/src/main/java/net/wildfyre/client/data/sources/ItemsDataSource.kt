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
        fetcher(failureHandler, params.requestedStartPosition, params.requestedLoadSize)?.let {
            callback.onResult(it.results!!, params.requestedStartPosition, it.count!!)
        }
        listener.onLoadingStop()
    }

    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<I>) {
        listener.onLoadingStart()
        fetcher(failureHandler, params.startPosition, params.loadSize)?.let {
            callback.onResult(it.results!!)
        }
        listener.onLoadingStop()
    }
}
