package app.fyreplace.client.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.paging.Config
import androidx.paging.DataSource
import androidx.paging.PagedList
import androidx.paging.toLiveData
import app.fyreplace.client.data.DataLoadingListener
import app.fyreplace.client.data.sources.ItemsDataSourceFactory
import app.fyreplace.client.lib.items_list.R

/**
 * Interface for ViewModels containing a list of items.
 */
abstract class ItemsListFragmentViewModel<I>(context: Context) : ViewModel(), DataLoadingListener {
    private var firstLoading = true
    private var mShouldRefresh = false
    private val mLoading = MutableLiveData<Boolean>()

    abstract val factory: ItemsDataSourceFactory<I>
    val dataSource: LiveData<DataSource<Int, I>> by lazy { factory.dataSource }
    val itemsPagedList: LiveData<PagedList<I>> by lazy {
        factory.toLiveData(Config(context.resources.getInteger(R.integer.post_preview_load_page_size)))
    }
    val loading: LiveData<Boolean> = mLoading
    val hasData: LiveData<Boolean> by lazy { itemsPagedList.map { it.size > 0 } }

    override fun onLoadingStart() {
        if (firstLoading) {
            firstLoading = false
            mLoading.postValue(true)
        }
    }

    override fun onLoadingStop() = mLoading.postValue(false)

    fun pushRefresh() {
        mShouldRefresh = true
    }

    fun popRefresh() = mShouldRefresh.also { mShouldRefresh = false }
}
