package app.fyreplace.client.ui

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.lifecycleScope
import app.fyreplace.client.lib.R
import app.fyreplace.client.viewmodels.CentralViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.getViewModel
import retrofit2.HttpException
import kotlin.coroutines.CoroutineContext

interface FailureHandler : LifecycleOwner, ViewModelStoreOwner {
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
            when (e.code()) {
                401 -> getViewModel<CentralViewModel>().logout()
                403 -> showBanAlert()
                else -> onFailure(e)
            }
        } catch (e: Exception) {
            onFailure(e)
        }
    }

    private fun showBanAlert() {
        val ctx = getContext() ?: return
        MaterialAlertDialogBuilder(ctx)
            .setIcon(R.drawable.ic_warning)
            .setTitle(R.string.failure_ban_dialog_title)
            .setMessage(R.string.failure_ban_dialog_message)
            .setPositiveButton(R.string.ok, null)
            .show()
    }
}
