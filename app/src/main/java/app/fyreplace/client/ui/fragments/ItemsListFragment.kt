package app.fyreplace.client.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.observe
import app.fyreplace.client.R
import app.fyreplace.client.databinding.FragmentItemsListBinding
import app.fyreplace.client.ui.adapters.EmptyItemsAdapter
import app.fyreplace.client.ui.adapters.ItemsAdapter
import app.fyreplace.client.viewmodels.ItemsListFragmentViewModel
import kotlinx.android.synthetic.main.fragment_items_list.*

/**
 * Base class for fragments displaying a list of items.
 */
abstract class ItemsListFragment<I, VM : ItemsListFragmentViewModel<I>, A : ItemsAdapter<I>> :
    FailureHandlingFragment(R.layout.fragment_items_list), ItemsAdapter.OnItemsChangedListener,
    ItemsAdapter.OnItemClickedListener<I> {
    abstract override val viewModel: VM
    abstract val itemsAdapter: A
    private var itemsRefreshCallback: ((Refresh) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentItemsListBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            loading = viewModel.loading
            hasData = viewModel.hasData
        }

        binding.itemsList.setHasFixedSize(true)
        binding.itemsList.adapter = itemsAdapter.apply {
            onItemsChangedListener = this@ItemsListFragment
            onItemClickedListener = this@ItemsListFragment
            viewModel.itemsPagedList.observe(viewLifecycleOwner) {
                submitList(it)
                viewModel.setHasData(it.size > 0)
            }
        }

        binding.refresher.setColorSchemeResources(R.color.colorPrimary)
        binding.refresher.setProgressBackgroundColorSchemeResource(R.color.colorBackground)
        binding.refresher.setOnRefreshListener { refreshItems(Refresh.NORMAL) }
        viewModel.loading.observe(viewLifecycleOwner) { binding.refresher.isRefreshing = it }
        viewModel.dataSource.observe(viewLifecycleOwner) {
            itemsRefreshCallback = { mode ->
                if (mode == Refresh.FULL) {
                    binding.itemsList.swapAdapter(EmptyItemsAdapter<I>(), false)
                }

                it.invalidate()
            }

            if (viewModel.popRefresh()) {
                refreshItems(Refresh.BACKGROUND)
            }
        }

        return binding.root
    }

    override fun onFailure(failure: Throwable) {
        super.onFailure(failure)
        refresher?.isRefreshing = false
    }

    override fun onItemsChanged() {
        if (items_list.adapter != itemsAdapter) {
            items_list.swapAdapter(itemsAdapter, false)
        }
    }

    override fun onItemClicked(item: I) = viewModel.pushRefresh()

    fun refreshItems(mode: Refresh) = itemsRefreshCallback?.let {
        if (mode != Refresh.BACKGROUND) {
            refresher?.isRefreshing = true
        }

        it(mode)
    }

    enum class Refresh {
        BACKGROUND,
        NORMAL,
        FULL
    }
}
