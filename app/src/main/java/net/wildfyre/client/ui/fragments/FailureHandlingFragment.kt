package net.wildfyre.client.ui.fragments

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import net.wildfyre.client.data.FailureHandler
import net.wildfyre.client.viewmodels.FailureHandlingViewModel

/**
 * Base [Fragment] class that handles errors from a [FailureHandlingViewModel].
 */
abstract class FailureHandlingFragment(contentLayoutId: Int) : Fragment(contentLayoutId), FailureHandler,
    CoroutineScope by CoroutineScope(SupervisorJob() + Dispatchers.Main) {
    /**
     * Subclasses must have a viewModel inheriting from [FailureHandlingViewModel]. It has to be initialized in
     * [onAttach] to it can then be used in [onCreate].
     */
    protected abstract val viewModels: List<FailureHandlingViewModel>

    override fun onAttach(context: Context) {
        super.onAttach(context)
        setHasOptionsMenu(true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModels.forEach { vm -> vm.lastFailure.observe(this, Observer { onFailure(it) }) }
    }

    override fun onDestroy() {
        cancel()
        super.onDestroy()
    }

    @CallSuper
    override fun onFailure(failure: Throwable) {
        super.onFailure(failure)
        context?.let { Toast.makeText(it, failure.localizedMessage, Toast.LENGTH_SHORT).show() }
    }
}
