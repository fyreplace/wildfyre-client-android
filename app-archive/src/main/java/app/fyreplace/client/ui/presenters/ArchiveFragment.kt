package app.fyreplace.client.ui.presenters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import app.fyreplace.client.app.archive.R
import app.fyreplace.client.data.models.Post
import app.fyreplace.client.ui.adapters.PostsAdapter
import app.fyreplace.client.viewmodels.ArchiveFragmentViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

/**
 * [androidx.fragment.app.Fragment] listing the user's subscribed posts.
 */
class ArchiveFragment : PostsFragment<ArchiveFragmentViewModel>(false) {
    override val viewModel by viewModel<ArchiveFragmentViewModel>()
    override val itemsAdapter = PostsAdapter(true)
    private val navigator by inject<Navigator> { parametersOf(this) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = super.onCreateView(inflater, container, savedInstanceState)
        .apply { bd.text.setText(R.string.archive_empty) }

    override fun onItemClicked(item: Post) {
        super.onItemClicked(item)
        navigator.navigateToPost(item)
    }

    interface Navigator {
        fun navigateToPost(post: Post)
    }
}
