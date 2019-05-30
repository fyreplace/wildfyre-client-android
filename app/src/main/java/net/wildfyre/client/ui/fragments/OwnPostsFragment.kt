package net.wildfyre.client.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import net.wildfyre.client.R
import net.wildfyre.client.ui.adapters.PostsAdapter
import net.wildfyre.client.viewmodels.OwnPostsFragmentViewModel
import net.wildfyre.client.viewmodels.lazyViewModel

/**
 * [androidx.fragment.app.Fragment] listing the user's own posts.
 */
class OwnPostsFragment : PostsFragment<OwnPostsFragmentViewModel>() {
    override val viewModel by lazyViewModel<OwnPostsFragmentViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return onCreateView(inflater, container, PostsAdapter(false))
            ?.apply { findViewById<TextView>(R.id.text).setText(R.string.posts_empty) }
    }
}
