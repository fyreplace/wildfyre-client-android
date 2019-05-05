package net.wildfyre.client.views

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import net.wildfyre.client.R
import net.wildfyre.client.data.Post
import net.wildfyre.client.viewmodels.ArchiveFragmentViewModel
import net.wildfyre.client.viewmodels.AreaSelectingFragmentViewModel
import net.wildfyre.client.viewmodels.FailureHandlingViewModel
import net.wildfyre.client.views.adapters.PostsAdapter

/**
 * [androidx.fragment.app.Fragment] listing the user's subscribed posts.
 */
class ArchiveFragment : ItemsListFragment<ArchiveFragmentViewModel, Post>(), AreaSelectingFragment {
    override lateinit var viewModel: ArchiveFragmentViewModel
    override lateinit var areaSelectingViewModel: AreaSelectingFragmentViewModel
    override val viewModels: List<FailureHandlingViewModel>
        get() = listOf(viewModel, areaSelectingViewModel)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = ViewModelProviders.of(this).get(ArchiveFragmentViewModel::class.java)
        areaSelectingViewModel = ViewModelProviders.of(activity!!).get(AreaSelectingFragmentViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super<ItemsListFragment>.onCreate(savedInstanceState)
        onCreate(this)
        areaSelectingViewModel.preferredArea.observe(this, Observer { onRefresh() })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return onCreateView(inflater, container, PostsAdapter(true), savedInstanceState == null)
            ?.apply { findViewById<TextView>(R.id.text).setText(R.string.archive_empty) }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_posts_actions, menu)
        onCreateOptionsMenu(this, menu)
    }
}