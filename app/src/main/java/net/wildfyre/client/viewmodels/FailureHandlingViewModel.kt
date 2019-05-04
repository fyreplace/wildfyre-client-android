package net.wildfyre.client.viewmodels

import android.app.Application
import androidx.annotation.CallSuper
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import net.wildfyre.client.data.Failure
import net.wildfyre.client.data.FailureHandler

/**
 * Base [androidx.lifecycle.ViewModel] class to use with [net.wildfyre.client.views.FailureHandlingActivity] or
 * [net.wildfyre.client.views.FailureHandlingFragment] that implements basic error propagation.
 */
abstract class FailureHandlingViewModel(application: Application) : AndroidViewModel(application), FailureHandler {
    private var _lastFailure = MutableLiveData<Failure>()

    val lastFailure: LiveData<Failure>
        get() = _lastFailure

    @CallSuper
    override fun onFailure(failure: Failure) {
        _lastFailure.value = failure
    }
}