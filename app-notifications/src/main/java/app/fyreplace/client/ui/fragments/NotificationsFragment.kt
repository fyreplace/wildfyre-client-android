package app.fyreplace.client.ui.fragments

import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.observe
import app.fyreplace.client.data.models.Notification
import app.fyreplace.client.lib.notifications.R
import app.fyreplace.client.lib.notifications.databinding.ActionNotificationsClearBinding
import app.fyreplace.client.ui.adapters.NotificationsAdapter
import app.fyreplace.client.viewmodels.CentralViewModel
import app.fyreplace.client.viewmodels.NotificationsFragmentViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

/**
 * [androidx.fragment.app.Fragment] listing the user's notifications.
 */
class NotificationsFragment :
    ItemsListFragment<Notification, NotificationsFragmentViewModel, NotificationsAdapter>() {
    override val viewModel by viewModel<NotificationsFragmentViewModel>()
    override val itemsAdapter by lazy { NotificationsAdapter(requireContext()) }
    private val centralViewModel by sharedViewModel<CentralViewModel>()
    private val navigator by inject<Navigator> { parametersOf(this) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) =
        super.onCreateView(inflater, container, savedInstanceState)
            .apply { findViewById<TextView>(R.id.text).setText(R.string.notifications_empty) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.itemsPagedList.observe(viewLifecycleOwner) {
            centralViewModel.forceNotificationCount(it.size)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.actions_fragment_notifications, menu)
        val clear = menu.findItem(R.id.action_clear).actionView

        ActionNotificationsClearBinding.bind(clear).run {
            lifecycleOwner = viewLifecycleOwner
            hasData = viewModel.hasData
        }

        clear.findViewById<View>(R.id.button).setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.notifications_action_clear_dialog_title))
                .setNegativeButton(R.string.no, null)
                .setPositiveButton(R.string.yes) { _: DialogInterface, _: Int -> launch { viewModel.clearNotifications() } }
                .show()
        }
    }

    override fun onItemClicked(item: Notification) {
        super.onItemClicked(item)
        navigator.navigateToPost(item.area, item.post.id, item.comments)
    }

    interface Navigator {
        fun navigateToPost(areaName: String, postId: Long, newCommentsIds: List<Long>)
    }
}
