package net.wildfyre.client.views

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import net.wildfyre.client.R
import net.wildfyre.client.data.Post
import net.wildfyre.client.databinding.FragmentPostsBinding
import net.wildfyre.client.viewmodels.AreaSelectingFragmentViewModel
import net.wildfyre.client.viewmodels.FailureHandlingViewModel
import net.wildfyre.client.viewmodels.PostsFragmentViewModel
import net.wildfyre.client.views.adapters.PostsAdapter

/**
 * [androidx.fragment.app.Fragment] listing the user's own posts.
 */
class PostsFragment : ItemsListFragment<PostsFragmentViewModel, Post>(R.layout.fragment_posts), AreaSelectingFragment {
    override lateinit var viewModel: PostsFragmentViewModel
    override lateinit var areaSelectingViewModel: AreaSelectingFragmentViewModel
    override val viewModels: List<FailureHandlingViewModel>
        get() = listOf(viewModel, areaSelectingViewModel)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        areaSelectingViewModel = ViewModelProviders.of(activity!!).get(AreaSelectingFragmentViewModel::class.java)
        viewModel = ViewModelProviders.of(this).get(PostsFragmentViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super<ItemsListFragment>.onCreate(savedInstanceState)
        onCreate(this)
        areaSelectingViewModel.preferredArea.observe(this, Observer { onRefresh() })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = FragmentPostsBinding.inflate(inflater, container, false).run {
            lifecycleOwner = this@PostsFragment
            model = viewModel
            root
        }

        return onCreateView(root, PostsAdapter(false), savedInstanceState == null)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_posts_actions, menu)
        onCreateOptionsMenu(this, menu)
    }
}