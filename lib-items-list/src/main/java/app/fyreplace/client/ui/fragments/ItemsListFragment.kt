package app.fyreplace.client.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.observe
import app.fyreplace.client.lib.items_list.R
import app.fyreplace.client.lib.items_list.databinding.FragmentItemsListBinding
import app.fyreplace.client.ui.adapters.EmptyItemsAdapter
import app.fyreplace.client.ui.adapters.ItemsAdapter
import app.fyreplace.client.viewmodels.ItemsListFragmentViewModel

/**
 * Base class for fragments displaying a list of items.
 */
abstract class ItemsListFragment<I, VM : ItemsListFragmentViewModel<I>, A : ItemsAdapter<I>> :
    FailureHandlingFragment(R.layout.fragment_items_list), ItemsAdapter.OnItemsChangedListener,
    ItemsAdapter.OnItemClickedListener<I> {
    abstract override val viewModel: VM
    override lateinit var bd: FragmentItemsListBinding
    protected abstract val itemsAdapter: A
    private var itemsRefreshCallback: ((Refresh) -> Unit)? = null

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

        bd.itemsList.setHasFixedSize(true)
        bd.itemsList.adapter = itemsAdapter.apply {
            onItemsChangedListener = this@ItemsListFragment
            onItemClickedListener = this@ItemsListFragment
            viewModel.itemsPagedList.observe(viewLifecycleOwner) { submitList(it) }
        }

        bd.refresher.setColorSchemeResources(R.color.colorPrimary)
        bd.refresher.setProgressBackgroundColorSchemeResource(R.color.colorBackground)
        bd.refresher.setOnRefreshListener { refreshItems(Refresh.NORMAL) }
        viewModel.loading.observe(viewLifecycleOwner) { bd.refresher.isRefreshing = it }
        viewModel.dataSource.observe(viewLifecycleOwner) {
            itemsRefreshCallback = { mode ->
                if (mode == Refresh.FULL) {
                    bd.itemsList.swapAdapter(EmptyItemsAdapter<I>(), false)
                }

                it.invalidate()
            }

            if (viewModel.popRefresh()) {
                refreshItems(Refresh.BACKGROUND)
            }
        }

        return bd.root
    }

    override fun onFailure(failure: Throwable) {
        super.onFailure(failure)
        bd.refresher.isRefreshing = false
    }

    override fun onItemsChanged() {
        if (bd.itemsList.adapter != itemsAdapter) {
            bd.itemsList.swapAdapter(itemsAdapter, false)
        }
    }

    override fun onItemClicked(item: I) = viewModel.pushRefresh()

    fun refreshItems(mode: Refresh) = itemsRefreshCallback?.let {
        if (mode != Refresh.BACKGROUND) {
            bd.refresher.isRefreshing = true
        }

        it(mode)
    }

    enum class Refresh {
        BACKGROUND,
        NORMAL,
        FULL
    }
}
