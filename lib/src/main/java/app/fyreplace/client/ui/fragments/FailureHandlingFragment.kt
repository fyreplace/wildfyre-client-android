package app.fyreplace.client.ui.fragments

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import app.fyreplace.client.ui.FailureHandler
import app.fyreplace.client.ui.Presenter
import kotlinx.coroutines.cancel

abstract class FailureHandlingFragment(contentLayoutId: Int) : Fragment(contentLayoutId),
    FailureHandler, Presenter {
    protected abstract val viewModel: ViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        setHasOptionsMenu(true)
    }

    override fun onDestroy() {
        lifecycleScope.cancel()
        super.onDestroy()
    }
}
