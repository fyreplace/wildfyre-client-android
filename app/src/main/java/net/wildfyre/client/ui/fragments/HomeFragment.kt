package net.wildfyre.client.ui.fragments

import android.view.Menu
import android.view.MenuInflater
import net.wildfyre.client.viewmodels.*

/**
 * [androidx.fragment.app.Fragment] for showing new posts to the user.
 */
class HomeFragment : PostFragment(), AreaSelectingFragment {
    override val viewModels: List<FailureHandlingViewModel> by lazy {
        super.viewModels + listOf(viewModel, areaSelectingViewModel)
    }
    override val areaSelectingViewModel by lazyActivityViewModel<AreaSelectingFragmentViewModel>()
    private val viewModel by lazyViewModel<HomeFragmentViewModel>()

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        onCreateOptionsMenu(this, menu, inflater)
        super<PostFragment>.onCreateOptionsMenu(menu, inflater)
    }
}
