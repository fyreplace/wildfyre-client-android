package net.wildfyre.client.viewmodels

import android.app.Application
import androidx.annotation.CallSuper
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import net.wildfyre.client.data.Failure
import net.wildfyre.client.data.FailureHandler
import net.wildfyre.client.data.SingleLiveEvent

/**
 * Base [androidx.lifecycle.ViewModel] class to use with [net.wildfyre.client.views.FailureHandlingActivity] or
 * [net.wildfyre.client.views.FailureHandlingFragment] that implements basic error propagation.
 */
abstract class FailureHandlingViewModel(application: Application) : AndroidViewModel(application), FailureHandler {
    private var _lastFailure = SingleLiveEvent<Failure>()

    val lastFailure: LiveData<Failure>
        get() = _lastFailure

    @CallSuper
    override fun onFailure(failure: Failure) {
        super.onFailure(failure)
        _lastFailure.value = failure
    }
}
