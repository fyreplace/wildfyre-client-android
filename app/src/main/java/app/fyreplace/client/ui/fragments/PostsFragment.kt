package app.fyreplace.client.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.lifecycle.ViewModel
import androidx.lifecycle.observe
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.RecyclerView
import app.fyreplace.client.R
import app.fyreplace.client.data.models.Post
import app.fyreplace.client.ui.adapters.PostsAdapter
import app.fyreplace.client.ui.widgets.ItemIdKeyProvider
import app.fyreplace.client.ui.widgets.PostDetailsLookup
import app.fyreplace.client.viewmodels.AreaSelectingFragmentViewModel
import app.fyreplace.client.viewmodels.PostsFragmentViewModel
import kotlinx.android.synthetic.main.fragment_items_list.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

/**
 * [androidx.fragment.app.Fragment] listing posts.
 */
abstract class PostsFragment<VM : PostsFragmentViewModel>(private val hasSelection: Boolean) :
    ItemsListFragment<Post, VM, PostsAdapter>(), AreaSelectingFragment, ActionMode.Callback {
    override val viewModels: List<ViewModel> by lazy { listOf(viewModel, areaSelectingViewModel) }
    override val areaSelectingViewModel by sharedViewModel<AreaSelectingFragmentViewModel>()
    private var settingUp = true
    private var selectionObserver: SelectionObserver? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        areaSelectingViewModel.preferredAreaName.observe(this) {
            if (settingUp) {
                settingUp = false
            } else {
                refreshItems(Refresh.FULL)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = super.onCreateView(inflater, container, savedInstanceState).also {
        if (hasSelection) {
            val itemsList = it.findViewById<RecyclerView>(R.id.items_list)
            itemsAdapter.selectionTracker = SelectionTracker.Builder(
                SELECTION_TRACKER_ID,
                itemsList,
                ItemIdKeyProvider(itemsList),
                PostDetailsLookup(itemsList),
                StorageStrategy.createLongStorage()
            ).build().apply {
                onRestoreInstanceState(savedInstanceState)
                selectionObserver = SelectionObserver()
                addObserver(selectionObserver)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        itemsAdapter.selectionTracker?.onSaveInstanceState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)


        if (itemsAdapter.selectionTracker?.hasSelection() == true) {
            selectionObserver?.startActionMode()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.actions_fragment_posts, menu)
        onCreateOptionsMenu(this, menu, inflater)
    }

    override fun onItemClicked(item: Post) {
        super.onItemClicked(item)
        itemsAdapter.selectionTracker?.clearSelection()
    }

    override fun onCreateActionMode(mode: ActionMode, menu: Menu) =
        mode.menuInflater.inflate(R.menu.actions_fragment_deletion, menu).let { true }

    override fun onPrepareActionMode(mode: ActionMode, menu: Menu) =
        itemsAdapter.selectionTracker?.selection?.size()?.let {
            mode.title = resources
                .getQuantityString(R.plurals.posts_action_mode_selection_title, it, it)
            true
        } ?: false

    override fun onActionItemClicked(mode: ActionMode, item: MenuItem) = when (item.itemId) {
        R.id.action_delete -> deleteSelection(mode).let { true }
        else -> false
    }

    override fun onDestroyActionMode(mode: ActionMode) {
        itemsAdapter.selectionTracker?.clearSelection()
    }

    private fun deleteSelection(mode: ActionMode) {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.posts_action_delete_dialog_title)
            .setNegativeButton(R.string.no, null)
            .setPositiveButton(R.string.yes) { _, _ ->
                launch {
                    itemsAdapter.selectionTracker?.selection?.forEach { viewModel.delete(it) }
                    mode.finish()
                    refreshItems(Refresh.BACKGROUND)
                }
            }
            .show()
    }

    private companion object {
        const val SELECTION_TRACKER_ID = "selection.posts"
    }

    private inner class SelectionObserver : SelectionTracker.SelectionObserver<Long>() {
        private var actionMode: ActionMode? = null

        override fun onItemStateChanged(key: Long, selected: Boolean) {
            val count = itemsAdapter.selectionTracker?.selection?.size()

            if (count == 1 && selected) {
                startActionMode()
            }

            actionMode?.invalidate()

            if (count == 0) {
                actionMode?.finish()
            }
        }

        fun startActionMode() {
            actionMode =
                (activity as? AppCompatActivity)?.startSupportActionMode(this@PostsFragment)
        }
    }
}
