package app.fyreplace.client.ui

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import app.fyreplace.client.lib.R
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import kotlin.coroutines.CoroutineContext

interface FailureHandler : LifecycleOwner {
    val viewModel: ViewModel

    fun getContext(): Context?

    fun onFailure(failure: Throwable) {
        getContext()?.run {
            Log.e(getString(R.string.app_name), failure.message.orEmpty())
            Toast.makeText(
                this,
                getString(R.string.failure_toast, failure.localizedMessage),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    fun launch(
        context: CoroutineContext = Dispatchers.Main,
        block: suspend CoroutineScope.() -> Unit
    ) = lifecycleScope.launch(context) {
        try {
            block()
        } catch (e: CancellationException) {
            // Cancellation happens
        } catch (e: HttpException) {
            if (e.code() == 403) {
                showBanAlert()
            } else {
                onFailure(e)
            }
        } catch (e: Exception) {
            onFailure(e)
        }
    }

    fun showBanAlert() {
        val ctx = getContext() ?: return
        AlertDialog.Builder(ctx)
            .setIcon(R.drawable.ic_warning)
            .setTitle(R.string.failure_ban_dialog_title)
            .setMessage(R.string.failure_ban_dialog_message)
            .setPositiveButton(R.string.ok, null)
            .show()
    }
}
