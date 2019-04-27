package net.wildfyre.client.views

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.android.synthetic.main.fragment_notifications.*
import net.wildfyre.client.R
import net.wildfyre.client.data.Failure
import net.wildfyre.client.databinding.FragmentNotificationsBinding
import net.wildfyre.client.viewmodels.NotificationsFragmentViewModel
import net.wildfyre.client.views.adapters.NotificationsAdapter

class NotificationsFragment : FailureHandlingFragment(R.layout.fragment_notifications),
    SwipeRefreshLayout.OnRefreshListener {
    override lateinit var viewModel: NotificationsFragmentViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        setHasOptionsMenu(true)
        viewModel = ViewModelProviders.of(this).get(NotificationsFragmentViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        activity?.setTitle(R.string.main_nav_fragment_notifications)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = FragmentNotificationsBinding.inflate(inflater, container, false).run {
            lifecycleOwner = this@NotificationsFragment
            model = viewModel
            root
        }

        val notificationList = root.findViewById<RecyclerView>(R.id.notification_list)
        val swipeRefresh = root.findViewById<SwipeRefreshLayout>(R.id.refresher)

        notificationList.adapter = NotificationsAdapter().apply {
            viewModel.superNotification.observe(this@NotificationsFragment, Observer {
                val notifications = it.results!!

                if (it.count!!.toInt() == notifications.size) {
                    setData(notifications)
                    swipeRefresh.isRefreshing = false
                }
            })
        }

        swipeRefresh.setColorSchemeResources(R.color.colorAccent)
        swipeRefresh.setProgressBackgroundColorSchemeResource(R.color.background)
        swipeRefresh.setOnRefreshListener(this)
        swipeRefresh.isRefreshing = true
        onRefresh()
        return root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_notifications_actions, menu)
        menu.findItem(R.id.action_clear).actionView?.setOnClickListener {
            AlertDialog.Builder(context!!)
                .setTitle(getString(R.string.notifications_dialog_title))
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok) { _: DialogInterface, _: Int -> viewModel.clearNotifications() }
                .show()
        }
    }

    override fun onFailure(failure: Failure) {
        super.onFailure(failure)
        refresher.isRefreshing = false
    }

    override fun onRefresh() {
        viewModel.fetchNotifications()
    }
}