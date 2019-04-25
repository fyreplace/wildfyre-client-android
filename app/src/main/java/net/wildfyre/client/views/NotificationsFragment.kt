package net.wildfyre.client.views

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import net.wildfyre.client.R
import net.wildfyre.client.databinding.FragmentNotificationsBinding
import net.wildfyre.client.viewmodels.NotificationsFragmentViewModel
import net.wildfyre.client.views.adapters.NotificationsAdapter

class NotificationsFragment : FailureHandlingFragment(R.layout.fragment_notifications) {
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
        notificationList.adapter = NotificationsAdapter().apply {
            viewModel.superNotification.observe(this@NotificationsFragment, Observer {
                mergeData(it.results!!, viewModel.currentOffset.toInt())
            })
        }

        viewModel.updateNotifications()
        return root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_notifications_actions, menu)
        menu.findItem(R.id.action_clear).actionView?.setOnClickListener {
            AlertDialog.Builder(context!!)
                .setTitle(getString(R.string.notifications_dialog_clear))
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok) { _: DialogInterface, _: Int -> viewModel.clearNotifications() }
                .show()
        }
    }
}