package net.wildfyre.client.data.sources

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import net.wildfyre.client.data.FailureHandler

abstract class ItemsDataSourceFactory<I>(protected val failureHandler: FailureHandler) : DataSource.Factory<Int, I>() {
    private val _dataSource = MutableLiveData<DataSource<Int, I>>()

    val dataSource: LiveData<DataSource<Int, I>> = _dataSource

    override fun create(): DataSource<Int, I> = newSource().also { _dataSource.postValue(it) }

    protected abstract fun newSource(): ItemsDataSource<I>
}
