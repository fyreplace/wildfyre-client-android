package app.fyreplace.client.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.distinctUntilChanged
import androidx.paging.Config
import androidx.paging.DataSource
import androidx.paging.PagedList
import androidx.paging.toLiveData
import app.fyreplace.client.FyreplaceApplication
import app.fyreplace.client.R
import app.fyreplace.client.data.sources.DataLoadingListener
import app.fyreplace.client.data.sources.ItemsDataSourceFactory

/**
 * Interface for ViewModels containing a list of items.
 */
abstract class ItemsListFragmentViewModel<I> : ViewModel(), DataLoadingListener {
    private var firstLoading = true
    private val mLoading = MutableLiveData<Boolean>()
    private val mHasData = MutableLiveData<Boolean>()

    abstract val factory: ItemsDataSourceFactory<I>
    val dataSource: LiveData<DataSource<Int, I>> by lazy { factory.dataSource }
    val itemsPagedList: LiveData<PagedList<I>> by lazy {
        val pageSize = FyreplaceApplication.context.resources.getInteger(R.integer.post_preview_load_page_size)
        return@lazy factory.toLiveData(
            Config(
                enablePlaceholders = true,
                pageSize = pageSize,
                initialLoadSizeHint = pageSize
            )
        )
    }
    val loading: LiveData<Boolean> = mLoading
    val hasData: LiveData<Boolean> = mHasData.distinctUntilChanged()

    override fun onLoadingStart() {
        if (firstLoading) {
            firstLoading = false
            mLoading.postValue(true)
        }
    }

    override fun onLoadingStop() = mLoading.postValue(false)

    fun setHasData(hasSome: Boolean) = mHasData.postValue(hasSome)
}
