package app.fyreplace.client.ui.activities

import android.widget.Toast
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import app.fyreplace.client.data.FailureHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

abstract class FailureHandlingActivity : AppCompatActivity(), FailureHandler,
    CoroutineScope by CoroutineScope(SupervisorJob() + Dispatchers.Main) {
    protected abstract val viewModel: ViewModel

    override fun onDestroy() {
        cancel()
        super.onDestroy()
    }

    @CallSuper
    override fun onFailure(failure: Throwable) {
        super.onFailure(failure)
        Toast.makeText(this, failure.localizedMessage, Toast.LENGTH_SHORT).show()
    }
}
