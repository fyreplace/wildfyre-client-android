package app.fyreplace.client.viewmodels

import android.app.Application
import androidx.annotation.CallSuper
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import app.fyreplace.client.data.FailureHandler
import app.fyreplace.client.data.SingleLiveEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

/**
 * Base [androidx.lifecycle.ViewModel] class to use with [app.fyreplace.client.ui.activities.FailureHandlingActivity] or
 * [app.fyreplace.client.ui.fragments.FailureHandlingFragment] that implements basic error propagation.
 */
abstract class FailureHandlingViewModel(application: Application) : AndroidViewModel(application), FailureHandler {
    private var mFailureEvent = SingleLiveEvent<Throwable>()

    val failureEvent: LiveData<Throwable>
        get() = mFailureEvent

    @CallSuper
    override fun onFailure(failure: Throwable) {
        super.onFailure(failure)
        mFailureEvent.postValue(failure)
    }

    fun launchCatching(context: CoroutineContext = Dispatchers.Main, block: suspend CoroutineScope.() -> Unit) =
        viewModelScope.launch(context) {
            try {
                block()
            } catch (e: Exception) {
                onFailure(e)
            }
        }
}
