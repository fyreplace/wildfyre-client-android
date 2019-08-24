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
    protected var onRefreshListener: SwipeRefreshLayout.OnRefreshListener? = null
    abstract val itemsAdapter: A

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
        itemsList.adapter = itemsAdapter.apply {
            onItemClickedListener = this@ItemsListFragment
            viewModel.itemsPagedList.observe(viewLifecycleOwner) {
                submitList(it)
                viewModel.setHasData(it.size > 0)
            }
        }

        swipeRefresh.setColorSchemeResources(R.color.colorPrimary)
        swipeRefresh.setProgressBackgroundColorSchemeResource(R.color.colorBackground)
        viewModel.loading.observe(viewLifecycleOwner) { swipeRefresh.isRefreshing = it }
        viewModel.dataSource.observe(viewLifecycleOwner) {
            onRefreshListener = SwipeRefreshLayout.OnRefreshListener(it::invalidate)
            swipeRefresh.setOnRefreshListener(onRefreshListener)
        }

        return root
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)

        if (viewModel.popRefresh()) {
            onRefreshListener?.onRefresh()
        }
    }

    override fun onFailure(failure: Throwable) {
        super.onFailure(failure)
        refresher.isRefreshing = false
    }

    override fun onItemClicked(item: I) = viewModel.pushRefresh()
}
