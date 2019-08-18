package app.fyreplace.client.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import app.fyreplace.client.R
import app.fyreplace.client.ui.adapters.PostsAdapter
import app.fyreplace.client.viewmodels.OwnPostsFragmentViewModel
import app.fyreplace.client.viewmodels.lazyViewModel

/**
 * [androidx.fragment.app.Fragment] listing the user's own posts.
 */
class OwnPostsFragment : PostsFragment<OwnPostsFragmentViewModel>() {
    override val itemsAdapter = PostsAdapter(false)
    override val viewModel by lazyViewModel<OwnPostsFragmentViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        super.onCreateView(inflater, container, savedInstanceState)
            .apply { findViewById<TextView>(R.id.text).setText(R.string.own_posts_empty) }
}
