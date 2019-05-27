package net.wildfyre.client.views

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.fragment_item_list.*
import net.wildfyre.client.R
import net.wildfyre.client.data.Post
import net.wildfyre.client.viewmodels.AreaSelectingFragmentViewModel
import net.wildfyre.client.viewmodels.FailureHandlingViewModel
import net.wildfyre.client.viewmodels.PostsFragmentViewModel
import net.wildfyre.client.viewmodels.lazyActivityViewModel

/**
 * [androidx.fragment.app.Fragment] listing posts.
 */
abstract class PostsFragment<VM : PostsFragmentViewModel> : ItemsListFragment<VM, Post>(), AreaSelectingFragment {
    override val viewModels: List<FailureHandlingViewModel> by lazy { listOf(viewModel, areaSelectingViewModel) }
    override val areaSelectingViewModel by lazyActivityViewModel<AreaSelectingFragmentViewModel>()
    private var settingUp = true

    override fun onAttach(context: Context) {
        super<ItemsListFragment>.onAttach(context)
        onAttach(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
