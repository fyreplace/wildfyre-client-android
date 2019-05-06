package net.wildfyre.client.views

import android.content.Context
import android.view.Menu
import android.view.MenuInflater
import androidx.lifecycle.ViewModelProviders
import net.wildfyre.client.R
import net.wildfyre.client.viewmodels.AreaSelectingFragmentViewModel
import net.wildfyre.client.viewmodels.FailureHandlingViewModel
import net.wildfyre.client.viewmodels.HomeFragmentViewModel

/**
 * [androidx.fragment.app.Fragment] for showing new posts to the user.
 */
class HomeFragment : FailureHandlingFragment(R.layout.fragment_home), AreaSelectingFragment {
    private lateinit var viewModel: HomeFragmentViewModel
    override lateinit var areaSelectingViewModel: AreaSelectingFragmentViewModel
    override val viewModels: List<FailureHandlingViewModel>
        get() = listOf(viewModel, areaSelectingViewModel)

    override fun onAttach(context: Context) {
        super<FailureHandlingFragment>.onAttach(context)
        onAttach(this)
        activity?.title = ""
        viewModel = ViewModelProviders.of(this).get(HomeFragmentViewModel::class.java)
        areaSelectingViewModel = ViewModelProviders.of(activity!!).get(AreaSelectingFragmentViewModel::class.java)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_home_actions, menu)
        onCreateOptionsMenu(this, menu)
    }
}