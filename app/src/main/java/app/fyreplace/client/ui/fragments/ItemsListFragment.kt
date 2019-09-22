package app.fyreplace.client.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.observe
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import app.fyreplace.client.R
import app.fyreplace.client.databinding.FragmentItemsListBinding
import app.fyreplace.client.ui.adapters.ItemsAdapter
import app.fyreplace.client.viewmodels.ItemsListFragmentViewModel
import kotlinx.android.synthetic.main.fragment_items_list.*

/**
 * Base class for fragments displaying a list of items.
 */
abstract class ItemsListFragment<I, VM : ItemsListFragmentViewModel<I>, A : ItemsAdapter<I>> :
    FailureHandlingFragment(R.layout.fragment_items_list), ItemsAdapter.OnItemClickedListener<I> {
    abstract override val viewModel: VM
    @Suppress("UNCHECKED_CAST")
    val itemsAdapter: A?
        get() = items_list?.adapter as? A
    private var itemsRefreshCallback: ((Boolean) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = FragmentItemsListBinding.inflate(inflater, container, false).run {
            lifecycleOwner = viewLifecycleOwner
            loading = viewModel.loading
            hasData = viewModel.hasData
            return@run root
        }

        val itemsList = root.findViewById<RecyclerView>(R.id.items_list)
        val swipeRefresh = root.findViewById<SwipeRefreshLayout>(R.id.refresher)

        itemsList.setHasFixedSize(true)
        itemsList.setupAdapter()
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary)
        swipeRefresh.setProgressBackgroundColorSchemeResource(R.color.colorBackground)
        swipeRefresh.setOnRefreshListener { refreshItems(false) }

        viewModel.loading.observe(viewLifecycleOwner) { swipeRefresh?.isRefreshing = it }
        viewModel.dataSource.observe(viewLifecycleOwner) {
            itemsRefreshCallback = { clear ->
                if (clear) {
                    items_list?.setupAdapter()
                }

                it.invalidate()
            }

            if (viewModel.popRefresh()) {
                refreshItems(false)
            }
        }
        viewModel.itemsPagedList.observe(viewLifecycleOwner) {
            itemsAdapter?.submitList(it)
            viewModel.setHasData(it.size > 0)
        }

        return root
    }

    override fun onFailure(failure: Throwable) {
        super.onFailure(failure)
        refresher?.isRefreshing = false
    }

    override fun onItemClicked(item: I) = viewModel.pushRefresh()

    abstract fun createAdapter(): A

    fun refreshItems(clear: Boolean) = itemsRefreshCallback?.let {
        if (clear) {
            refresher?.isRefreshing = true
        }

        it(clear)
    }

    private fun RecyclerView.setupAdapter() {
        adapter = createAdapter().apply { onItemClickedListener = this@ItemsListFragment }
    }
}
