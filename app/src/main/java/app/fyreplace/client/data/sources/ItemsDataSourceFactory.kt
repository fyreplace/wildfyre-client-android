package app.fyreplace.client.data.sources

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource

abstract class ItemsDataSourceFactory<I> : DataSource.Factory<Int, I>() {
    private val mDataSource = MutableLiveData<DataSource<Int, I>>()

    val dataSource: LiveData<DataSource<Int, I>> = mDataSource

    override fun create(): DataSource<Int, I> = newSource().also { mDataSource.postValue(it) }

    protected abstract fun newSource(): ItemsDataSource<I>
}
