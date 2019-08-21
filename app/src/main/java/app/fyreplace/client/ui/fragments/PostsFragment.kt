package app.fyreplace.client.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.lifecycle.ViewModel
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StableIdKeyProvider
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.RecyclerView
import app.fyreplace.client.NavigationMainDirections
import app.fyreplace.client.R
import app.fyreplace.client.data.models.Post
import app.fyreplace.client.ui.adapters.PostDetailsLookup
import app.fyreplace.client.ui.adapters.PostsAdapter
import app.fyreplace.client.viewmodels.AreaSelectingFragmentViewModel
import app.fyreplace.client.viewmodels.ItemsListFragmentViewModel
import app.fyreplace.client.viewmodels.lazyActivityViewModel
import kotlinx.android.synthetic.main.fragment_items_list.*

/**
 * [androidx.fragment.app.Fragment] listing posts.
 */
abstract class PostsFragment<VM : ItemsListFragmentViewModel<Post>> :
    ItemsListFragment<Post, VM, PostsAdapter>(), AreaSelectingFragment, ActionMode.Callback {
    override val viewModels: List<ViewModel> by lazy { listOf(viewModel, areaSelectingViewModel) }
    override val areaSelectingViewModel by lazyActivityViewModel<AreaSelectingFragmentViewModel>()
    private var settingUp = true

    override fun onAttach(context: Context) {
        super.onAttach(context)
        areaSelectingViewModel.preferredAreaName.observe(this) {
            if (settingUp) {
                settingUp = false
            } else {
                refresher?.isRefreshing = true
                onRefreshListener?.onRefresh()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) =
        super.onCreateView(inflater, container, savedInstanceState).also {
            val itemsList = it.findViewById<RecyclerView>(R.id.items_list)
            itemsAdapter.selectionTracker = SelectionTracker.Builder(
                SELECTION_TRACKER_ID,
                itemsList,
                StableIdKeyProvider(itemsList),
                PostDetailsLookup(itemsList),
                StorageStrategy.createLongStorage()
            ).build().apply {
                onRestoreInstanceState(savedInstanceState)
                addObserver(SelectionObserver())
            }
        }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        itemsAdapter.selectionTracker?.onSaveInstanceState(outState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_posts_actions, menu)
        onCreateOptionsMenu(this, menu, inflater)
    }

    override fun onItemClicked(item: Post) {
        itemsAdapter.selectionTracker?.clearSelection()
        findNavController().navigate(NavigationMainDirections.actionGlobalFragmentPost(post = item))
    }

    override fun onCreateActionMode(mode: ActionMode, menu: Menu) =
        mode.menuInflater.inflate(R.menu.fragment_posts_action_mode_selection, menu).let { true }

    override fun onPrepareActionMode(mode: ActionMode, menu: Menu) = false

    override fun onActionItemClicked(mode: ActionMode, item: MenuItem) = when (item.itemId) {
        R.id.action_delete -> {
            Toast.makeText(context, R.string.posts_actions_delete, Toast.LENGTH_SHORT).show()
            true
        }
        else -> false
    }

    override fun onDestroyActionMode(mode: ActionMode) {
        itemsAdapter.selectionTracker?.clearSelection()
    }

    private companion object {
        const val SELECTION_TRACKER_ID = "selection.posts"
    }

    private inner class SelectionObserver : SelectionTracker.SelectionObserver<Long>() {
        private var count = 0
        private var actionMode: ActionMode? = null

        override fun onItemStateChanged(key: Long, selected: Boolean) {
            if (count == 0) {
                actionMode =
                    (activity as? AppCompatActivity)?.startSupportActionMode(this@PostsFragment)
            }

            count += if (selected) 1 else -1

            if (count == 0) {
                actionMode?.finish()
            }
        }
    }
}
