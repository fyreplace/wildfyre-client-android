package app.fyreplace.client.ui.presenters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import app.fyreplace.client.data.models.Model
import app.fyreplace.client.lib.items_list.R
import app.fyreplace.client.lib.items_list.databinding.FragmentItemsListBinding
import app.fyreplace.client.ui.Presenter
import app.fyreplace.client.ui.adapters.EmptyItemsAdapter
import app.fyreplace.client.ui.adapters.ItemsAdapter
import app.fyreplace.client.viewmodels.ItemsListFragmentViewModel
import app.fyreplace.client.viewmodels.Refresh

/**
 * Base class for fragments displaying a list of items.
 */
abstract class ItemsListFragment<I : Model, VM : ItemsListFragmentViewModel<I>, A : ItemsAdapter<I>> :
    Fragment(R.layout.fragment_items_list), Presenter, ItemsAdapter.OnItemsChangedListener,
    ItemsAdapter.OnItemClickedListener<I> {
    abstract override val viewModel: VM
    override lateinit var bd: FragmentItemsListBinding
    protected abstract val itemsAdapter: A
    private var itemsRefreshCallback: (() -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bd = FragmentItemsListBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            loading = viewModel.loading
            hasData = viewModel.hasData
        }

        with(bd.itemsList) {
            if (viewModel.firstLoading) {
                bd.refresher.isRefreshing = true
            }

            setHasFixedSize(true)
            adapter = itemsAdapter.apply {
                onItemsChangedListener = this@ItemsListFragment
                onItemClickedListener = this@ItemsListFragment
                viewModel.itemsPagedList.observe(viewLifecycleOwner) { submitList(it) }
            }
        }

        with(bd.refresher) {
            setColorSchemeResources(R.color.colorSecondary)
            setProgressBackgroundColorSchemeResource(R.color.colorBackground)
            setOnRefreshListener { refreshItems(Refresh.NORMAL) }
        }

        viewModel.dataSource.observe(viewLifecycleOwner) {
            itemsRefreshCallback = { it.invalidate() }

            if (viewModel.popRefresh()) {
                refreshItems(Refresh.BACKGROUND)
            }
        }

        viewModel.refreshMode.observe(viewLifecycleOwner) { mode ->
            val showingItems = bd.itemsList.adapter == itemsAdapter

            when {
                mode != Refresh.FULL && !showingItems ->
                    bd.itemsList.swapAdapter(itemsAdapter, false)
                mode == Refresh.FULL && showingItems ->
                    bd.itemsList.swapAdapter(EmptyItemsAdapter<I>(), false)
            }

            when {
                mode == null -> bd.refresher.isRefreshing = false
                mode != Refresh.BACKGROUND -> bd.refresher.isRefreshing = true
            }
        }

        return bd.root
    }

    override fun onFailure(failure: Throwable) {
        super.onFailure(failure)
        bd.refresher.isRefreshing = false
    }

    override fun onItemsChanged() {
        if (viewModel.hasRefreshedItems) {
            viewModel.setRefreshMode(null)
        }
    }

    override fun onItemClicked(item: I) = viewModel.pushRefresh()

    fun refreshItems(mode: Refresh) = itemsRefreshCallback?.let {
        viewModel.setRefreshMode(mode)
        it()
    }
}
