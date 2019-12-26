package app.fyreplace.client.ui.presenters

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import app.fyreplace.client.ui.FailureHandler
import app.fyreplace.client.ui.Presenter
import kotlinx.coroutines.cancel

abstract class FailureHandlingActivity(contentLayoutId: Int) : AppCompatActivity(contentLayoutId),
    FailureHandler, Presenter {
    override fun onDestroy() {
        lifecycleScope.cancel()
        super.onDestroy()
    }

    override fun getContext() = this
}
