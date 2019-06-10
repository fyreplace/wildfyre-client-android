package net.wildfyre.client.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.core.view.forEach
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.post_buttons.*
import net.wildfyre.client.R
import net.wildfyre.client.databinding.ActionsAreaReputationBinding
import net.wildfyre.client.databinding.ActionsAreaSpreadBinding
import net.wildfyre.client.viewmodels.*

/**
 * [androidx.fragment.app.Fragment] for showing new posts to the user.
 */
class HomeFragment : PostFragment(), AreaSelectingFragment {
    override val viewModels: List<FailureHandlingViewModel> by lazy { super.viewModels + areaSelectingViewModel }
    override val areaSelectingViewModel by lazyActivityViewModel<AreaSelectingFragmentViewModel>()
    override val viewModel by lazyViewModel<HomeFragmentViewModel>()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        areaSelectingViewModel.preferredAreaName.observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                viewModel.nextPostAsync(it)
            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        super.onCreateView(inflater, container, savedInstanceState)
            .apply { findViewById<View>(R.id.buttons).isVisible = true }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        extinguish.setOnClickListener { viewModel.spreadAsync(false) }
        ignite.setOnClickListener { viewModel.spreadAsync(true) }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_home_actions, menu)
        onCreateOptionsMenu(this, menu, inflater)

        ActionsAreaSpreadBinding.bind(menu.findItem(R.id.action_area_spread).actionView).run {
            lifecycleOwner = viewLifecycleOwner
            model = areaSelectingViewModel
        }

        ActionsAreaReputationBinding.bind(menu.findItem(R.id.action_area_reputation).actionView).run {
            lifecycleOwner = viewLifecycleOwner
            model = areaSelectingViewModel
        }

        menu.findItem(R.id.action_area_selector)?.actionView
            ?.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
                private val areaStuff = listOf(R.id.action_area_spread, R.id.action_area_reputation)
                private val mainActivityViewModel =
                    ViewModelProviders.of(requireActivity()).get(MainActivityViewModel::class.java)

                override fun onViewAttachedToWindow(v: View?) = switchItems(true)

                override fun onViewDetachedFromWindow(v: View?) = switchItems(false)

                private fun switchItems(showAreaStuff: Boolean) {
                    menu.forEach { it.isVisible = areaStuff.contains(it.itemId) == showAreaStuff }
                    mainActivityViewModel.setNotificationBadgeVisible(!showAreaStuff)
                }
            })

        super<PostFragment>.onCreateOptionsMenu(menu, inflater)
        val showAsAction =
            if (resources.getBoolean(R.bool.home_show_menu))
                MenuItem.SHOW_AS_ACTION_IF_ROOM
            else
                MenuItem.SHOW_AS_ACTION_NEVER

        for (id in setOf(R.id.action_area_selector, R.id.action_share, R.id.action_subscribe)) {
            menu.findItem(id).setShowAsAction(showAsAction or MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW)
        }
    }
}
