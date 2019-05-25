package net.wildfyre.client.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import net.wildfyre.client.R
import net.wildfyre.client.viewmodels.ArchiveFragmentViewModel
import net.wildfyre.client.viewmodels.lazyViewModel
import net.wildfyre.client.views.adapters.PostsAdapter

/**
 * [androidx.fragment.app.Fragment] listing the user's subscribed posts.
 */
class ArchiveFragment : PostsFragment<ArchiveFragmentViewModel>() {
    override val viewModel by lazyViewModel<ArchiveFragmentViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return onCreateView(inflater, container, PostsAdapter(true), savedInstanceState)
            ?.apply { findViewById<TextView>(R.id.text).setText(R.string.archive_empty) }
    }
}
