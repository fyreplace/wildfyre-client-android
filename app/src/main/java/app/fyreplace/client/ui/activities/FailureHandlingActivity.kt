package app.fyreplace.client.ui.activities

import android.os.Bundle
import android.widget.Toast
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import app.fyreplace.client.data.FailureHandler
import app.fyreplace.client.viewmodels.FailureHandlingViewModel

/**
 * Base [android.app.Activity] class that handles errors from a [FailureHandlingViewModel].
 */
abstract class FailureHandlingActivity : AppCompatActivity(), FailureHandler {
    protected abstract val viewModel: FailureHandlingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.failureEvent.observe(this, Observer { onFailure(it) })
    }

    @CallSuper
    override fun onFailure(failure: Throwable) {
        super.onFailure(failure)
        Toast.makeText(this, failure.localizedMessage, Toast.LENGTH_SHORT).show()
    }
}
