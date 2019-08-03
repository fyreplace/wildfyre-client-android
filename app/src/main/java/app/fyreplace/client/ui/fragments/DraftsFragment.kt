package app.fyreplace.client.ui.fragments

import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import app.fyreplace.client.R
import app.fyreplace.client.data.models.Post
import app.fyreplace.client.ui.adapters.PostsAdapter
import app.fyreplace.client.viewmodels.DraftsFragmentViewModel
import app.fyreplace.client.viewmodels.lazyViewModel

/**
 * [androidx.fragment.app.Fragment] listing the user's drafts.
 */
class DraftsFragment : PostsFragment<DraftsFragmentViewModel>() {
    override val viewModel by lazyViewModel<DraftsFragmentViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        super.onCreateView(inflater, container, savedInstanceState)
            .apply { findViewById<TextView>(R.id.text).setText(R.string.drafts_empty) }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_drafts_actions, menu)
        super.onCreateOptionsMenu(menu, inflater)
        menu.findItem(R.id.action_new).actionView?.findViewById<Button>(R.id.button)?.setOnClickListener {
            Toast.makeText(requireContext(), R.string.main_nav_fragment_drafts, Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemsAdapter(): PostsAdapter = PostsAdapter(false)

    override fun onItemClicked(item: Post) {
        Toast.makeText(requireContext(), R.string.main_nav_fragment_drafts, Toast.LENGTH_SHORT).show()
    }
}
