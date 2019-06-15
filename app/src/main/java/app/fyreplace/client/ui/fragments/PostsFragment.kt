package app.fyreplace.client.ui.fragments

import android.content.Context
import android.view.Menu
import android.view.MenuInflater
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import app.fyreplace.client.NavigationMainDirections
import app.fyreplace.client.R
import app.fyreplace.client.data.models.Post
import app.fyreplace.client.ui.adapters.PostsAdapter
import app.fyreplace.client.viewmodels.AreaSelectingFragmentViewModel
import app.fyreplace.client.viewmodels.FailureHandlingViewModel
import app.fyreplace.client.viewmodels.ItemsListFragmentViewModel
import app.fyreplace.client.viewmodels.lazyActivityViewModel
import kotlinx.android.synthetic.main.fragment_items_list.*

/**
 * [androidx.fragment.app.Fragment] listing posts.
 */
abstract class PostsFragment<VM : ItemsListFragmentViewModel<Post>> : ItemsListFragment<Post, VM, PostsAdapter>(),
    AreaSelectingFragment {
    override val viewModels: List<FailureHandlingViewModel> by lazy { listOf(viewModel, areaSelectingViewModel) }
    override val areaSelectingViewModel by lazyActivityViewModel<AreaSelectingFragmentViewModel>()
    private var settingUp = true

    override fun onAttach(context: Context) {
        super.onAttach(context)
        areaSelectingViewModel.preferredAreaName.observe(this, Observer {
            if (settingUp) {
                settingUp = false
                return@Observer
            }

            refresher?.isRefreshing = true
            onRefreshListener.onRefresh()
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_posts_actions, menu)
        onCreateOptionsMenu(this, menu, inflater)
    }

    override fun onItemClicked(item: Post) =
        findNavController().navigate(
            NavigationMainDirections.actionGlobalFragmentPost(
                areaName = areaSelectingViewModel.preferredAreaName.value.orEmpty(),
                postId = item.id
            )
        )
}
