package net.wildfyre.client.views

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import net.wildfyre.client.R
import net.wildfyre.client.viewmodels.NotificationsFragmentViewModel

class NotificationsFragment : FailureHandlingFragment(R.layout.fragment_notifications) {
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
        viewModel.updateNotifications()
        return super.onCreateView(inflater, container, savedInstanceState)
    }
}