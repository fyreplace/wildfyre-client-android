package net.wildfyre.client.views

import android.content.Context
import android.view.Menu
import android.view.MenuInflater
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.navArgs
import kotlinx.android.synthetic.main.fragment_post.*
import net.wildfyre.client.R
import net.wildfyre.client.viewmodels.FailureHandlingViewModel
import net.wildfyre.client.viewmodels.PostFragmentViewModel

class PostFragment : FailureHandlingFragment(R.layout.fragment_post) {
    private lateinit var viewModel: PostFragmentViewModel
    override val viewModels: List<FailureHandlingViewModel>
        get() = listOf(viewModel)
    private val args by navArgs<PostFragmentArgs>()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        setHasOptionsMenu(true)
        viewModel = ViewModelProviders.of(this).get(PostFragmentViewModel::class.java)
        viewModel.setPostId(args.postId)
        viewModel.post.observe(this, Observer { text.text = it.text })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_post_actions, menu)
    }
}