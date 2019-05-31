package net.wildfyre.client.viewmodels

import android.app.Application
import androidx.annotation.CallSuper
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import net.wildfyre.client.data.FailureHandler
import net.wildfyre.client.data.SingleLiveEvent

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
        _lastFailure.value = failure
    }
}
