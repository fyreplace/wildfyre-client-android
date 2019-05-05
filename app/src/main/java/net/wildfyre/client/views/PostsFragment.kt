package net.wildfyre.client.views

import android.content.Context
import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import net.wildfyre.client.R
import net.wildfyre.client.viewmodels.FailureHandlingViewModel
import net.wildfyre.client.viewmodels.PostsFragmentViewModel

/**
 * [androidx.fragment.app.Fragment] listing the user's own posts.
 */
class PostsFragment : FailureHandlingFragment(R.layout.fragment_posts) {
    override val viewModels: List<FailureHandlingViewModel>
        get() = listOf(viewModel)
    private lateinit var viewModel: PostsFragmentViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = ViewModelProviders.of(this).get(PostsFragmentViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        activity?.setTitle(R.string.main_nav_fragment_posts)
    }
}