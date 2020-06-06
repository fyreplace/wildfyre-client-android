package app.fyreplace.client.viewmodels

import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.paging.Config
import androidx.paging.PagedList
import androidx.paging.toLiveData
import app.fyreplace.client.data.DataLoadingListener
import app.fyreplace.client.data.models.Model
import app.fyreplace.client.data.sources.ItemsDataSource
import app.fyreplace.client.data.sources.ItemsDataSourceFactory
import app.fyreplace.client.lib.items_list.R

/**
 * Interface for ViewModels containing a list of items.
 */
abstract class ItemsListFragmentViewModel<I : Model>(resources: Resources) : ViewModel(),
    DataLoadingListener {
    private var mFirstLoading = true
    private var mHasRefreshedItems = false
    private var mShouldRefresh = false
    private var mLoadingCount = 0
    private val mLoading = MutableLiveData<Boolean>()
    private val mRefreshMode = MutableLiveData<Refresh?>()

    abstract val factory: ItemsDataSourceFactory<I>
    val dataSource: LiveData<ItemsDataSource<I>> by lazy { factory.dataSource }
    val itemsPagedList: LiveData<PagedList<I>> by lazy {
        val pageSize = resources.getInteger(R.integer.post_preview_load_page_size)
        factory.toLiveData(
            Config(
                pageSize = pageSize,
                initialLoadSizeHint = pageSize,
                enablePlaceholders = false
            )
        )
    }
    val firstLoading: Boolean
        get() = mFirstLoading.also { if (mFirstLoading) mFirstLoading = false }
    val hasRefreshedItems: Boolean
        get() = mHasRefreshedItems.also { if (mHasRefreshedItems) mHasRefreshedItems = false }
    val loading: LiveData<Boolean> = mLoading
    val refreshMode: LiveData<Refresh?> = mRefreshMode
    val hasData: LiveData<Boolean> by lazy { itemsPagedList.map { it.size > 0 } }

    override fun onLoadingStart() = updateLoading(true)

    override fun onLoadingStop() = updateLoading(false).also { mHasRefreshedItems = true }

    fun pushRefresh() {
        mShouldRefresh = true
    }

    fun popRefresh() = mShouldRefresh.also { mShouldRefresh = false }

    fun setRefreshMode(refresh: Refresh?) = mRefreshMode.postValue(refresh)

    private fun updateLoading(start: Boolean) {
        mLoadingCount += if (start) 1 else -1
        mLoading.postValue(mLoadingCount > 0)
    }
}

enum class Refresh {
    BACKGROUND,
    NORMAL,
    FULL
}
