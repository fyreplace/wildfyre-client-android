package net.wildfyre.client.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.android.synthetic.main.fragment_item_list.*
import net.wildfyre.client.NavigationMainDirections
import net.wildfyre.client.R
import net.wildfyre.client.data.Failure
import net.wildfyre.client.databinding.FragmentItemListBinding
import net.wildfyre.client.viewmodels.ItemsListViewModel
import net.wildfyre.client.views.adapters.ItemsAdapter

/**
 * Base class for fragments displaying a list of items.
 */
abstract class ItemsListFragment<VM : ItemsListViewModel<I>, I> :
    FailureHandlingFragment(R.layout.fragment_item_list), RecyclerView.OnChildAttachStateChangeListener,
    SwipeRefreshLayout.OnRefreshListener, ItemsAdapter.OnItemClickedListener {
    /**
     * Indicates whether the next notification change is a manual reset.
     */
    private var resetting = false
    private var firstSetup = true
    abstract val viewModel: VM

    fun <A : ItemsAdapter<I>> onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        adapter: A,
        savedInstance: Bundle?
    ): View? {
        val root = FragmentItemListBinding.inflate(inflater, container, false)
            .run {
                lifecycleOwner = this@ItemsListFragment
                itemCount = viewModel.itemCount
                root
            }

        val itemsList = root.findViewById<RecyclerView>(R.id.items_list)
        val swipeRefresh = root.findViewById<SwipeRefreshLayout>(R.id.refresher)

        itemsList.adapter = adapter.apply {
            viewModel.items.observe(this@ItemsListFragment, Observer {
                val previousCount = data.size
                data = it

                if (resetting) {
                    resetting = false
                } else {
                    swipeRefresh.isRefreshing = false
                }

                if (it.isNotEmpty()) {
                    notifyDataSetChanged()

                    if (it.size < previousCount) {
                        fillList()
                    }
                } else {
                    notifyItemRangeRemoved(0, previousCount)
                }
            })

            onItemClickedListener = this@ItemsListFragment
        }

        // When the user scrolls, new notifications should be fetched dynamically
        itemsList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                // Only try to fill the list if the user is scrolling down and we haven't fetched all notifications yet
                if (dy > 0 && viewModel.itemCount.value!!.toInt() > recyclerView.layoutManager!!.itemCount) {
                    fillList()
                }
            }
        })

        itemsList.addOnChildAttachStateChangeListener(this)
        swipeRefresh.setColorSchemeResources(R.color.colorAccent)
        swipeRefresh.setProgressBackgroundColorSchemeResource(R.color.background)
        swipeRefresh.setOnRefreshListener(this)

        if (firstSetup && savedInstance == null) {
            swipeRefresh.isRefreshing = true
            onRefresh()
        }

        firstSetup = false
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        items_list.removeOnChildAttachStateChangeListener(this)
        items_list.clearOnScrollListeners()
    }

    override fun onFailure(failure: Failure) {
        super.onFailure(failure)
        refresher.isRefreshing = false
    }

    override fun onChildViewAttachedToWindow(view: View) {
        val layoutManager = items_list.layoutManager!! as StaggeredGridLayoutManager

        /*
        If all currently fetched notifications are being displayed, and there are more to fetch, then fetch more
        notifications to make sure that the view is completely filled with notifications.
         */
        if (layoutManager.childCount == layoutManager.itemCount && (viewModel.itemCount.value
                ?: 0) > layoutManager.itemCount
        ) {
            refresher.isRefreshing = true
            viewModel.fetchNextItems()
        }
    }

    override fun onChildViewDetachedFromWindow(view: View) {
    }

    override fun onRefresh() {
        resetting = true
        viewModel.resetItems()
        viewModel.fetchNextItems()
    }

    override fun onItemClicked(id: Long) {
        findNavController().navigate(NavigationMainDirections.actionGlobalFragmentPost(id))
    }

    private fun fillList() {
        val layoutManager = items_list.layoutManager!! as StaggeredGridLayoutManager
        val lastPosition = layoutManager.findLastVisibleItemPositions(IntArray(layoutManager.spanCount)).max() ?: 0

        /*
        If there are less notifications left to show, than the number currently displayed on screen, then fetch
        more notifications to show the user.
         */
        if (lastPosition + 1 >= layoutManager.itemCount - layoutManager.childCount && !refresher.isRefreshing) {
            refresher.isRefreshing = true
            viewModel.fetchNextItems()
        }
    }
}
