package app.fyreplace.client.ui.activities

import android.widget.Toast
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import app.fyreplace.client.data.FailureHandler
import kotlinx.coroutines.cancel

abstract class FailureHandlingActivity : AppCompatActivity(), FailureHandler {
    protected abstract val viewModel: ViewModel

    override fun onDestroy() {
        lifecycleScope.cancel()
        super.onDestroy()
    }

    @CallSuper
    override fun onFailure(failure: Throwable) {
        super.onFailure(failure)
        Toast.makeText(this, failure.localizedMessage, Toast.LENGTH_SHORT).show()
    }
}
