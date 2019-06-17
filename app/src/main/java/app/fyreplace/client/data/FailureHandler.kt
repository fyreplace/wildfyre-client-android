package app.fyreplace.client.data

import android.util.Log
import androidx.annotation.CallSuper
import app.fyreplace.client.FyreplaceApplication
import app.fyreplace.client.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

/**
 * Interface implemented by classes that can receive a [Throwable] and propagate it.
 */
interface FailureHandler : CoroutineScope {
    /**
     * Called whenever an operation failed.
     *
     * @param failure The object containing information on what went wrong
     */
    @CallSuper
    fun onFailure(failure: Throwable) {
        Log.e(FyreplaceApplication.context.getString(R.string.app_name), failure.message.orEmpty())
    }

    fun launchCatching(context: CoroutineContext = Dispatchers.Main, block: suspend CoroutineScope.() -> Unit) =
        launch(context) {
            try {
                block()
            } catch (e: Exception) {
                onFailure(e)
            }
        }
}
