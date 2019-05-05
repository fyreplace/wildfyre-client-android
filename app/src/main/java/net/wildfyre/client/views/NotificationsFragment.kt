package net.wildfyre.client.views

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import net.wildfyre.client.R
import net.wildfyre.client.data.Notification
import net.wildfyre.client.viewmodels.FailureHandlingViewModel
import net.wildfyre.client.viewmodels.NotificationsFragmentViewModel
import net.wildfyre.client.views.adapters.NotificationsAdapter

/**
 * [androidx.fragment.app.Fragment] listing the user's notifications.
 */
class NotificationsFragment :
    ItemsListFragment<NotificationsFragmentViewModel, Notification>(),
    RecyclerView.OnChildAttachStateChangeListener, SwipeRefreshLayout.OnRefreshListener {
    override val viewModels: List<FailureHandlingViewModel>
        get() = listOf(viewModel)
    override lateinit var viewModel: NotificationsFragmentViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = ViewModelProviders.of(this).get(NotificationsFragmentViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        activity?.setTitle(R.string.main_nav_fragment_notifications)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return onCreateView(inflater, container, NotificationsAdapter(), savedInstanceState == null)
            ?.apply { findViewById<TextView>(R.id.text).setText(R.string.notifications_empty) }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_notifications_actions, menu)
        menu.findItem(R.id.action_clear).actionView?.setOnClickListener {
            AlertDialog.Builder(context!!)
                .setTitle(getString(R.string.notifications_dialog_title))
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok) { _: DialogInterface, _: Int -> viewModel.clear() }
                .show()
        }
    }
}