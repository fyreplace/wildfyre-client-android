package app.fyreplace.client.ui.activities

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import app.fyreplace.client.ui.FailureHandler
import kotlinx.coroutines.cancel

abstract class FailureHandlingActivity : AppCompatActivity(),
    FailureHandler {
    protected abstract val viewModel: ViewModel

    override fun onDestroy() {
        lifecycleScope.cancel()
        super.onDestroy()
    }

    override fun getContext() = this
}
