package app.fyreplace.client.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import app.fyreplace.client.R
import app.fyreplace.client.ui.adapters.PostsAdapter
import app.fyreplace.client.viewmodels.ArchiveFragmentViewModel
import app.fyreplace.client.viewmodels.lazyViewModel

/**
 * [androidx.fragment.app.Fragment] listing the user's subscribed posts.
 */
class ArchiveFragment : PostsFragment<ArchiveFragmentViewModel>() {
    override val viewModel by lazyViewModel<ArchiveFragmentViewModel>()
    override val itemsAdapter = PostsAdapter(true)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) =
        super.onCreateView(inflater, container, savedInstanceState)
            .apply { findViewById<TextView>(R.id.text).setText(R.string.archive_empty) }
}
