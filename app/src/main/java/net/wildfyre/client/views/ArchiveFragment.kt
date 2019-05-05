package net.wildfyre.client.views

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import net.wildfyre.client.R
import net.wildfyre.client.data.Post
import net.wildfyre.client.databinding.FragmentArchiveBinding
import net.wildfyre.client.viewmodels.ArchiveFragmentViewModel
import net.wildfyre.client.viewmodels.FailureHandlingViewModel
import net.wildfyre.client.views.adapters.PostsAdapter

/**
 * [androidx.fragment.app.Fragment] listing the user's subscribed posts.
 */
class ArchiveFragment : ItemsListFragment<ArchiveFragmentViewModel, Post>(R.layout.fragment_archive) {
    override lateinit var viewModel: ArchiveFragmentViewModel
    override val viewModels: List<FailureHandlingViewModel>
        get() = listOf(viewModel)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = ViewModelProviders.of(this).get(ArchiveFragmentViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(false)
        activity?.setTitle(R.string.main_nav_fragment_archive)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = FragmentArchiveBinding.inflate(inflater, container, false).run {
            lifecycleOwner = this@ArchiveFragment
            model = viewModel
            root
        }

        return onCreateView(root, PostsAdapter(true), savedInstanceState == null)
    }
}