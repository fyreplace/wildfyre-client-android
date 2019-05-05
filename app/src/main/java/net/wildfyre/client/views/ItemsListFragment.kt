package net.wildfyre.client.views

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.android.synthetic.main.fragment_item_list.*
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
    SwipeRefreshLayout.OnRefreshListener {
    /**
     * Indicates whether the next notification change is a manual reset.
     */
    private var resetting = false
    abstract val viewModel: VM

    fun <A : ItemsAdapter<I>> onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        adapter: A,
        firstSetup: Boolean
    ): View? {
        val root = FragmentItemListBinding.inflate(inflater, container, false)
            .run {
                lifecycleOwner = this@ItemsListFragment
                itemCount = viewModel.itemCount
                root
            }

        val notificationList = root.findViewById<RecyclerView>(R.id.items_list)
        val swipeRefresh = root.findViewById<SwipeRefreshLayout>(R.id.refresher)

        notificationList.adapter = adapter.apply {
            viewModel.items.observe(this@ItemsListFragment, Observer {
                val previousCount = data.size
                data = it

                if (resetting) {
                    resetting = false
                } else {
                    swipeRefresh.isRefreshing = false
                }

                if (it.isNotEmpty()) {
                    notifyItemRangeInserted(previousCount, it.size - previousCount)
                } else {
                    notifyItemRangeRemoved(0, previousCount)
                }
            })
        }

        // When the user scrolls, new notifications should be fetched dynamically
        notificationList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val layoutManager = notificationList.layoutManager!! as StaggeredGridLayoutManager

                // No need to do anything if the user is scrolling up or if we have fetched all notifications already
                if (dy <= 0 || viewModel.itemCount.value!!.toInt() == layoutManager.itemCount) {
                    return
                }

                val lastPosition = layoutManager.findLastVisibleItemPositions(IntArray(layoutManager.spanCount)).max()!!

                /*
                If there are less notifications left to show, than the number currently displayed on screen, then fetch
                more notifications to show the user.
                 */
                if (lastPosition + 1 >= layoutManager.itemCount - layoutManager.childCount && !swipeRefresh.isRefreshing) {
                    swipeRefresh.isRefreshing = true
                    viewModel.fetchNextItems()
                }
            }
        })

        notificationList.addOnChildAttachStateChangeListener(this)
        swipeRefresh.setColorSchemeResources(R.color.colorAccent)
        swipeRefresh.setProgressBackgroundColorSchemeResource(R.color.background)
        swipeRefresh.setOnRefreshListener(this)

        if (firstSetup) {
            swipeRefresh.isRefreshing = true
            onRefresh()
        }

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
        if (layoutManager.childCount == layoutManager.itemCount && viewModel.itemCount.value!! > layoutManager.itemCount) {
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
}