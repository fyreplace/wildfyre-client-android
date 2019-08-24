package app.fyreplace.client.ui.fragments

import android.view.Menu
import android.view.MenuInflater
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.navArgs
import app.fyreplace.client.R
import app.fyreplace.client.viewmodels.DraftFragmentViewModel
import app.fyreplace.client.viewmodels.lazyViewModel

class DraftFragment : FailureHandlingFragment(R.layout.fragment_draft) {
    override val viewModels: List<ViewModel> by lazy { listOf(viewModel) }
    override val viewModel by lazyViewModel<DraftFragmentViewModel>()
    private val fragmentArgs by navArgs<DraftFragmentArgs>()

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) =
        inflater.inflate(R.menu.actions_fragment_draft, menu)
}
