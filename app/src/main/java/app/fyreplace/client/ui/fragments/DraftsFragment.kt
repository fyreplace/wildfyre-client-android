package app.fyreplace.client.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import app.fyreplace.client.NavigationMainDirections
import app.fyreplace.client.R
import app.fyreplace.client.data.models.Post
import app.fyreplace.client.ui.adapters.PostsAdapter
import app.fyreplace.client.viewmodels.DraftsFragmentViewModel
import app.fyreplace.client.viewmodels.MainActivityViewModel
import app.fyreplace.client.viewmodels.lazyActivityViewModel
import app.fyreplace.client.viewmodels.lazyViewModel

/**
 * [androidx.fragment.app.Fragment] listing the user's drafts.
 */
class DraftsFragment : PostsFragment<DraftsFragmentViewModel>() {
    override val viewModel by lazyViewModel<DraftsFragmentViewModel>()
    override val itemsAdapter = PostsAdapter(false)
    private val mainViewModel by lazyActivityViewModel<MainActivityViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = super.onCreateView(inflater, container, savedInstanceState).apply {
        mainViewModel.setAllowDraftCreation(false)
        findViewById<TextView>(R.id.text).setText(R.string.drafts_empty)
    }

    override fun onDestroyView() = super.onDestroyView()
        .also { mainViewModel.setAllowDraftCreation(true) }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.actions_fragment_drafts, menu)
        super.onCreateOptionsMenu(menu, inflater)
        menu.findItem(R.id.action_new).actionView?.findViewById<Button>(R.id.button)
            ?.setOnClickListener { launch { onItemClicked(viewModel.createDraft()) } }
    }

    override fun onItemClicked(item: Post) {
        super.onItemClicked(item)
        findNavController().navigate(NavigationMainDirections.actionGlobalFragmentDraft(draft = item))
    }
}
