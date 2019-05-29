package net.wildfyre.client.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.Config
import androidx.paging.DataSource
import androidx.paging.PagedList
import androidx.paging.toLiveData
import net.wildfyre.client.Constants
import net.wildfyre.client.data.sources.DataLoadingListener
import net.wildfyre.client.data.sources.ItemsDataSourceFactory

/**
 * Interface for ViewModels containing a list of items.
 */
abstract class ItemsListViewModel<I>(application: Application) : FailureHandlingViewModel(application),
    DataLoadingListener {
    private val _loading = MutableLiveData<Boolean>()
    private val _hasData = MutableLiveData<Boolean>()

    abstract val factory: ItemsDataSourceFactory<I>
    val dataSource: LiveData<DataSource<Int, I>> by lazy { factory.dataSource }
    val itemsPagedList: LiveData<PagedList<I>> by lazy {
        factory.toLiveData(
            Config(
                enablePlaceholders = false,
                pageSize = Constants.Api.PAGE_SIZE,
                initialLoadSizeHint = Constants.Api.PAGE_SIZE
            )
        )
    }
    val loading: LiveData<Boolean> = _loading
    val hasData: LiveData<Boolean> = _hasData

    override fun onLoadingStart() = _loading.postValue(true)

    override fun onLoadingStop() = _loading.postValue(false)

    fun setHasData(hasSome: Boolean) = _hasData.postValue(hasSome)
}
