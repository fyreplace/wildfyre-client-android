package net.wildfyre.client.views

import android.os.Bundle
import android.widget.Toast
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import net.wildfyre.client.data.Failure
import net.wildfyre.client.data.FailureHandler
import net.wildfyre.client.viewmodels.FailureHandlingViewModel

/**
 * Base [Fragment] class that handles errors from a [FailureHandlingViewModel].
 */
abstract class FailureHandlingFragment(contentLayoutId: Int) : Fragment(contentLayoutId), FailureHandler {
    /**
     * Subclasses must have a viewModel inheriting from [FailureHandlingViewModel]. It has to be initialized in
     * [onAttach] to it can then be used in [onCreate].
     */
    protected abstract val viewModels: List<FailureHandlingViewModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModels.forEach { vm -> vm.lastFailure.observe(this, Observer { onFailure(it) }) }
    }

    @CallSuper
    override fun onFailure(failure: Failure) {
        super.onFailure(failure)
        context?.let { Toast.makeText(it, getString(failure.error), Toast.LENGTH_SHORT).show() }
    }
}
