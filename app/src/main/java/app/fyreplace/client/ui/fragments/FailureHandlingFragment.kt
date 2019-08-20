package app.fyreplace.client.ui.fragments

import android.content.Context
import android.widget.Toast
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import app.fyreplace.client.data.FailureHandler
import kotlinx.coroutines.cancel

abstract class FailureHandlingFragment(contentLayoutId: Int) : Fragment(contentLayoutId),
    FailureHandler {
    protected abstract val viewModels: List<ViewModel>
    protected abstract val viewModel: ViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        setHasOptionsMenu(true)
    }

    override fun onDestroy() {
        lifecycleScope.cancel()
        super.onDestroy()
    }

    @CallSuper
    override fun onFailure(failure: Throwable) {
        super.onFailure(failure)
        context?.let { Toast.makeText(it, failure.localizedMessage, Toast.LENGTH_SHORT).show() }
    }
}
