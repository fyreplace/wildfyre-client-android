package net.wildfyre.client.ui.fragments

import android.content.Context
import android.view.Menu
import android.view.MenuInflater
import androidx.lifecycle.Observer
import net.wildfyre.client.viewmodels.*

/**
 * [androidx.fragment.app.Fragment] for showing new posts to the user.
 */
class HomeFragment : PostFragment(), AreaSelectingFragment {
    override val viewModels: List<FailureHandlingViewModel> by lazy {
        super.viewModels + listOf(viewModel, areaSelectingViewModel)
    }
    override val areaSelectingViewModel by lazyActivityViewModel<AreaSelectingFragmentViewModel>()
    override val viewModel by lazyViewModel<HomeFragmentViewModel>()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        areaSelectingViewModel.preferredAreaName.observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                viewModel.nextPostAsync()
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        onCreateOptionsMenu(this, menu, inflater)
        super<PostFragment>.onCreateOptionsMenu(menu, inflater)
    }
}
