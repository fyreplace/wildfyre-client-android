package net.wildfyre.client.views

import android.content.Context
import android.os.Bundle
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
    override val viewModels: List<FailureHandlingViewModel>
        get() = listOf(viewModel, areaSelectingViewModel)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        areaSelectingViewModel = ViewModelProviders.of(activity!!).get(AreaSelectingFragmentViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super<ItemsListFragment>.onCreate(savedInstanceState)
        onCreate(this)
        areaSelectingViewModel.preferredArea.observe(this, Observer { refresher.isRefreshing = true; onRefresh() })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_posts_actions, menu)
        onCreateOptionsMenu(this, menu)
    }
}