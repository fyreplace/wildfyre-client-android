package net.wildfyre.client.views

import android.content.Context
import android.view.Menu
import android.view.MenuInflater
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.fragment_item_list.*
import net.wildfyre.client.R
import net.wildfyre.client.data.Post
import net.wildfyre.client.viewmodels.AreaSelectingFragmentViewModel
import net.wildfyre.client.viewmodels.FailureHandlingViewModel
import net.wildfyre.client.viewmodels.PostsFragmentViewModel

/**
 * [androidx.fragment.app.Fragment] listing posts.
 */
abstract class PostsFragment<VM : PostsFragmentViewModel> : ItemsListFragment<VM, Post>(), AreaSelectingFragment {
    override lateinit var areaSelectingViewModel: AreaSelectingFragmentViewModel
    override val viewModels: List<FailureHandlingViewModel> by lazy { listOf(viewModel, areaSelectingViewModel) }
    private var settingUp = true

    override fun onAttach(context: Context) {
        super<ItemsListFragment>.onAttach(context)
        onAttach(this)
        areaSelectingViewModel = ViewModelProviders.of(activity!!).get(AreaSelectingFragmentViewModel::class.java)
        areaSelectingViewModel.preferredAreaName.observe(this, Observer {
            if (settingUp) {
                settingUp = false
                return@Observer
            }

            refresher.isRefreshing = true
            onRefresh()
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_posts_actions, menu)
        onCreateOptionsMenu(this, menu)
    }
}
