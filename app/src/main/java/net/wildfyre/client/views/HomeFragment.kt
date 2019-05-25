package net.wildfyre.client.views

import android.content.Context
import android.view.Menu
import android.view.MenuInflater
import net.wildfyre.client.R
import net.wildfyre.client.viewmodels.*

/**
 * [androidx.fragment.app.Fragment] for showing new posts to the user.
 */
class HomeFragment : FailureHandlingFragment(R.layout.fragment_home), AreaSelectingFragment {
    override val viewModels: List<FailureHandlingViewModel> by lazy { listOf(viewModel, areaSelectingViewModel) }
    override val areaSelectingViewModel by lazyActivityViewModel<AreaSelectingFragmentViewModel>()
    private val viewModel by lazyViewModel<HomeFragmentViewModel>()

    override fun onAttach(context: Context) {
        super<FailureHandlingFragment>.onAttach(context)
        onAttach(this)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_home_actions, menu)
        onCreateOptionsMenu(this, menu)
    }
}
