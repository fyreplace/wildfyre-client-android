package net.wildfyre.client.views

import android.os.Bundle
import android.widget.Toast
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import net.wildfyre.client.data.Failure
import net.wildfyre.client.data.FailureHandler
import net.wildfyre.client.viewmodels.FailureHandlingViewModel

abstract class FailureHandlingFragment(contentLayoutId: Int) : Fragment(contentLayoutId), FailureHandler {
    protected abstract val viewModel: FailureHandlingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.lastFailure.observe(this, Observer { onFailure(it) })
    }

    @CallSuper
    override fun onFailure(failure: Failure) {
        super.onFailure(failure)
        context?.let { Toast.makeText(it, getString(failure.error), Toast.LENGTH_SHORT).show() }
    }
}