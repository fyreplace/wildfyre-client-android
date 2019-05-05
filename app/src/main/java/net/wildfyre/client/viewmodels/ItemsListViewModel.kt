package net.wildfyre.client.viewmodels

import androidx.lifecycle.LiveData

/**
 * Interface for ViewModels containing a list of items.
 */
interface ItemsListViewModel<I> {
    val itemCount: LiveData<Long>
    val items: LiveData<List<I>>

    fun fetchNextItems()

    fun resetItems()
}