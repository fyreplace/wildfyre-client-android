package net.wildfyre.client.views

import android.content.Context
import androidx.lifecycle.ViewModelProviders
import net.wildfyre.client.R
import net.wildfyre.client.viewmodels.FailureHandlingViewModel
import net.wildfyre.client.viewmodels.HomeFragmentViewModel

/**
 * [androidx.fragment.app.Fragment] for showing new posts to the user.
 */
class HomeFragment : AreaSelectingFragment(R.layout.fragment_home, R.menu.fragment_home_actions) {
    override val viewModels: List<FailureHandlingViewModel>
        get() = listOf(areaSelectingViewModel, viewModel)
    private lateinit var viewModel: HomeFragmentViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = ViewModelProviders.of(this).get(HomeFragmentViewModel::class.java)
    }
}