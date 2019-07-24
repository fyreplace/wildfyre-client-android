package app.fyreplace.client.ui.fragments

import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import app.fyreplace.client.NavigationMainDirections
import app.fyreplace.client.R
import app.fyreplace.client.data.models.Notification
import app.fyreplace.client.databinding.NotificationsActionsClearBinding
import app.fyreplace.client.ui.adapters.NotificationsAdapter
import app.fyreplace.client.viewmodels.*

/**
 * [androidx.fragment.app.Fragment] listing the user's notifications.
 */
class NotificationsFragment : ItemsListFragment<Notification, NotificationsFragmentViewModel, NotificationsAdapter>() {
    override val viewModels: List<FailureHandlingViewModel> by lazy { listOf(viewModel) }
    override val viewModel by lazyViewModel<NotificationsFragmentViewModel>()
    private val mainViewModel by lazyActivityViewModel<MainActivityViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        super.onCreateView(inflater, container, savedInstanceState)
            .apply { findViewById<TextView>(R.id.text).setText(R.string.notifications_empty) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = viewModel.itemsPagedList.observe(
        viewLifecycleOwner,
        Observer { mainViewModel.forceNotificationCount(it?.size ?: 0) }
    )

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
                .setPositiveButton(R.string.yes) { _: DialogInterface, _: Int -> viewModel.clearNotificationsAsync() }
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
