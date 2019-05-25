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
import net.wildfyre.client.databinding.NotificationsActionsClearBinding
import net.wildfyre.client.viewmodels.FailureHandlingViewModel
import net.wildfyre.client.viewmodels.NotificationsFragmentViewModel
import net.wildfyre.client.views.adapters.NotificationsAdapter

/**
 * [androidx.fragment.app.Fragment] listing the user's notifications.
 */
class NotificationsFragment :
    ItemsListFragment<NotificationsFragmentViewModel, Notification>(),
    RecyclerView.OnChildAttachStateChangeListener, SwipeRefreshLayout.OnRefreshListener {
    override val viewModels: List<FailureHandlingViewModel> by lazy { listOf(viewModel) }
    override lateinit var viewModel: NotificationsFragmentViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        setHasOptionsMenu(true)
        viewModel = ViewModelProviders.of(this).get(NotificationsFragmentViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return onCreateView(inflater, container, NotificationsAdapter(), savedInstanceState)
            ?.apply { findViewById<TextView>(R.id.text).setText(R.string.notifications_empty) }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_notifications_actions, menu)
        val clear = menu.findItem(R.id.action_clear).actionView!!

        NotificationsActionsClearBinding.bind(clear).run {
            lifecycleOwner = this@NotificationsFragment
            notificationCount = viewModel.itemCount
        }

        clear.findViewById<View>(R.id.button).setOnClickListener {
            AlertDialog.Builder(context!!)
                .setTitle(getString(R.string.notifications_dialog_title))
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok) { _: DialogInterface, _: Int -> viewModel.clearItems() }
                .show()
        }
    }
}
