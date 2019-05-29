package net.wildfyre.client.data.repositories

import androidx.lifecycle.MutableLiveData
import net.wildfyre.client.R
import net.wildfyre.client.data.FailureHandler
import net.wildfyre.client.data.SuperItem
import net.wildfyre.client.data.then
import retrofit2.Call

class AccumulatorRepositoryDelegate<T> {
    private var fetchingContent = false
    val mutableSuperItem = MutableLiveData<SuperItem<T>>()
    val mutableItems = MutableLiveData<List<T>>()
    var offset = 0L

    init {
        resetItems()
    }

    fun fetchNextItems(call: Call<SuperItem<T>>, fh: FailureHandler, forContent: Boolean) {
        when {
            fetchingContent -> return
            forContent -> fetchingContent = true
        }

        call.then(fh, R.string.failure_request) {
            fetchingContent = false
            mutableSuperItem.value = it

            if (forContent) {
                it.results?.run {
                    offset += size
                    mutableItems.value = mutableItems.value!! + this
                }
            }
        }
    }

    fun removeItem(item: T) {
        mutableSuperItem.value = mutableSuperItem.value?.apply { count = count!! - 1 }
        mutableItems.value = mutableItems.value?.subtract(setOf(item))?.toList()
    }

    fun resetItems() {
        offset = 0
        mutableSuperItem.value = (mutableSuperItem.value ?: SuperItem()).apply { count = 0; results = listOf() }
        mutableItems.value = listOf()
    }

    companion object {
        const val BUCKET_SIZE = 24L
    }
}
