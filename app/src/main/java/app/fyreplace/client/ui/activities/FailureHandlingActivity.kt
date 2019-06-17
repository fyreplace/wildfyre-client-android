package app.fyreplace.client.ui.activities

import android.widget.Toast
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import app.fyreplace.client.data.FailureHandler

abstract class FailureHandlingActivity : AppCompatActivity(), FailureHandler {
    protected abstract val viewModel: ViewModel

    @CallSuper
    override fun onFailure(failure: Throwable) {
        super.onFailure(failure)
        Toast.makeText(this, failure.localizedMessage, Toast.LENGTH_SHORT).show()
    }
}
