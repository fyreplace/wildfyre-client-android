package net.wildfyre.client.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData

/**
 * Interface for ViewModels containing a list of items.
 */
abstract class ItemsListViewModel<I>(application: Application) : FailureHandlingViewModel(application) {
    abstract val itemCount: LiveData<Int>
    abstract val items: LiveData<List<I>>

    abstract fun fetchNextItems()

    abstract fun resetItems()
}
