package app.fyreplace.client.ui.fragments

import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModel
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import app.fyreplace.client.NavigationMainDirections
import app.fyreplace.client.R
import app.fyreplace.client.data.models.Notification
import app.fyreplace.client.databinding.NotificationsActionsClearBinding
import app.fyreplace.client.ui.adapters.NotificationsAdapter
import app.fyreplace.client.viewmodels.MainActivityViewModel
import app.fyreplace.client.viewmodels.NotificationsFragmentViewModel
import app.fyreplace.client.viewmodels.lazyActivityViewModel
import app.fyreplace.client.viewmodels.lazyViewModel

/**
 * [androidx.fragment.app.Fragment] listing the user's notifications.
 */
class NotificationsFragment : ItemsListFragment<Notification, NotificationsFragmentViewModel, NotificationsAdapter>() {
    override val viewModels: List<ViewModel> by lazy { listOf(viewModel) }
    override val viewModel by lazyViewModel<NotificationsFragmentViewModel>()
    private val mainViewModel by lazyActivityViewModel<MainActivityViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        super.onCreateView(inflater, container, savedInstanceState)
            .apply { findViewById<TextView>(R.id.text).setText(R.string.notifications_empty) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.itemsPagedList.observe(viewLifecycleOwner) { mainViewModel.forceNotificationCount(it.size) }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)

        if (viewModel.checkRefresh()) {
            onRefreshListener?.onRefresh()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_notifications_actions, menu)
        val clear = menu.findItem(R.id.action_clear).actionView!!

        NotificationsActionsClearBinding.bind(clear).run {
            lifecycleOwner = viewLifecycleOwner
            hasData = viewModel.hasData
        }

        clear.findViewById<View>(R.id.button).setOnClickListener {
            AlertDialog.Builder(context!!)
                .setTitle(getString(R.string.notifications_actions_clear_dialog_title))
                .setNegativeButton(R.string.no, null)
                .setPositiveButton(R.string.yes) { _: DialogInterface, _: Int -> launchCatching { viewModel.clearNotifications() } }
                .show()
        }
    }

    override fun getItemsAdapter(): NotificationsAdapter = NotificationsAdapter()

    override fun onItemClicked(item: Notification) {
        viewModel.enableRefresh()
        findNavController().navigate(
            NavigationMainDirections.actionGlobalFragmentPost(
                areaName = item.area,
                postId = item.post.id,
                selectedCommentId = -1,
                newCommentsIds = item.comments.toLongArray()
            )
        )
    }
}
