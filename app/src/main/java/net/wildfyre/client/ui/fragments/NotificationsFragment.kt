package net.wildfyre.client.ui.fragments

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import net.wildfyre.client.NavigationMainDirections
import net.wildfyre.client.R
import net.wildfyre.client.data.Notification
import net.wildfyre.client.databinding.NotificationsActionsClearBinding
import net.wildfyre.client.ui.adapters.NotificationsAdapter
import net.wildfyre.client.viewmodels.FailureHandlingViewModel
import net.wildfyre.client.viewmodels.NotificationsFragmentViewModel
import net.wildfyre.client.viewmodels.lazyViewModel

/**
 * [androidx.fragment.app.Fragment] listing the user's notifications.
 */
class NotificationsFragment :
    ItemsListFragment<Notification, NotificationsFragmentViewModel, NotificationsAdapter>(),
    RecyclerView.OnChildAttachStateChangeListener {
    override val viewModels: List<FailureHandlingViewModel> by lazy { listOf(viewModel) }
    override val viewModel by lazyViewModel<NotificationsFragmentViewModel>()
    private var shouldRefresh = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        super.onCreateView(inflater, container, savedInstanceState)
            .apply { findViewById<TextView>(R.id.text).setText(R.string.notifications_empty) }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        savedInstanceState?.let { shouldRefresh = it.getBoolean(SAVE_SHOULD_REFRESH, false) }

        if (shouldRefresh) {
            shouldRefresh = false
            onRefreshListener?.onRefresh()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(SAVE_SHOULD_REFRESH, shouldRefresh)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_notifications_actions, menu)
        val clear = menu.findItem(R.id.action_clear).actionView!!

        NotificationsActionsClearBinding.bind(clear).run {
            lifecycleOwner = this@NotificationsFragment
            hasData = viewModel.hasData
        }

        clear.findViewById<View>(R.id.button).setOnClickListener {
            AlertDialog.Builder(context!!)
                .setTitle(getString(R.string.notifications_dialog_title))
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok) { _: DialogInterface, _: Int -> viewModel.clearNotifications() }
                .show()
        }
    }

    override fun getItemsAdapter(): NotificationsAdapter = NotificationsAdapter()

    override fun onItemClicked(item: Notification) {
        shouldRefresh = true
        findNavController().navigate(
            NavigationMainDirections.actionGlobalFragmentPost(
                item.area,
                item.post?.id ?: -1,
                -1,
                item.comments?.toLongArray()
            )
        )
    }

    private companion object {
        const val SAVE_SHOULD_REFRESH = "save.shouldRefresh"
    }
}
