package net.wildfyre.client.ui.fragments

import android.content.Context
import android.view.Menu
import android.view.MenuInflater
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_items_list.*
import net.wildfyre.client.NavigationMainDirections
import net.wildfyre.client.R
import net.wildfyre.client.data.Post
import net.wildfyre.client.ui.adapters.PostsAdapter
import net.wildfyre.client.viewmodels.AreaSelectingFragmentViewModel
import net.wildfyre.client.viewmodels.FailureHandlingViewModel
import net.wildfyre.client.viewmodels.ItemsListViewModel
import net.wildfyre.client.viewmodels.lazyActivityViewModel

/**
 * [androidx.fragment.app.Fragment] listing posts.
 */
abstract class PostsFragment<VM : ItemsListViewModel<Post>> : ItemsListFragment<Post, VM, PostsAdapter>(),
    AreaSelectingFragment {
    override val viewModels: List<FailureHandlingViewModel> by lazy { listOf(viewModel, areaSelectingViewModel) }
    override val areaSelectingViewModel by lazyActivityViewModel<AreaSelectingFragmentViewModel>()
    private var settingUp = true

    override fun onAttach(context: Context) {
        super<ItemsListFragment>.onAttach(context)
        onAttach(this)
        areaSelectingViewModel.preferredAreaName.observe(this, Observer {
            if (settingUp) {
                settingUp = false
                return@Observer
            }

            refresher?.isRefreshing = true
            onRefreshListener?.onRefresh()
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_posts_actions, menu)
        onCreateOptionsMenu(this, menu)
    }

    override fun onItemClicked(item: Post) =
        findNavController().navigate(NavigationMainDirections.actionGlobalFragmentPost(null, item.id))
}
