package net.wildfyre.client.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import net.wildfyre.client.data.Failure
import net.wildfyre.client.data.FailureHandler

abstract class FailureHandlingViewModel(application: Application) : AndroidViewModel(application), FailureHandler {
    private var _lastFailure = MutableLiveData<Failure>()

    val lastFailure: LiveData<Failure>
        get() = _lastFailure

    override fun onFailure(failure: Failure) {
        _lastFailure.value = failure
    }
}