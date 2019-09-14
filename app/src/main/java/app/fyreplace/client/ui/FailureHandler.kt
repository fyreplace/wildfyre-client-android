package app.fyreplace.client.ui

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import app.fyreplace.client.R
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

/**
 * Interface implemented by classes that can receive a [Throwable] and propagate it.
 */
interface FailureHandler : LifecycleOwner {
    fun getContext(): Context?

    fun onFailure(failure: Throwable) {
        getContext()?.let {
            Log.e(it.getString(R.string.app_name), failure.message.orEmpty())
            Toast.makeText(it, failure.localizedMessage, Toast.LENGTH_LONG).show()
        }
    }

    fun launch(
        context: CoroutineContext = Dispatchers.Main,
        block: suspend CoroutineScope.() -> Unit
    ) =
        lifecycleScope.launch(context) {
            try {
                block()
            } catch (e: CancellationException) {
                // Cancellation happens
            } catch (e: Exception) {
                onFailure(e)
            }
        }
}
