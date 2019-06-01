package net.wildfyre.client.viewmodels

import android.app.Application
import androidx.annotation.CallSuper
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.wildfyre.client.data.FailureHandler
import net.wildfyre.client.data.SingleLiveEvent
import kotlin.coroutines.CoroutineContext

/**
 * Base [androidx.lifecycle.ViewModel] class to use with [net.wildfyre.client.ui.activities.FailureHandlingActivity] or
 * [net.wildfyre.client.ui.fragments.FailureHandlingFragment] that implements basic error propagation.
 */
abstract class FailureHandlingViewModel(application: Application) : AndroidViewModel(application), FailureHandler {
    private var _lastFailure = SingleLiveEvent<Throwable>()

    val lastFailure: LiveData<Throwable>
        get() = _lastFailure

    @CallSuper
    override fun onFailure(failure: Throwable) {
        super.onFailure(failure)
        _lastFailure.postValue(failure)
    }

    fun launchCatching(context: CoroutineContext = Dispatchers.IO, block: suspend CoroutineScope.() -> Unit) =
        viewModelScope.launch(context) {
            try {
                block()
            } catch (e: Exception) {
                onFailure(e)
            }
        }
}
