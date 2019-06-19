package app.fyreplace.client.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.core.view.forEach
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModel
import androidx.lifecycle.observe
import app.fyreplace.client.R
import app.fyreplace.client.databinding.ActionsAreaReputationBinding
import app.fyreplace.client.databinding.ActionsAreaSpreadBinding
import app.fyreplace.client.viewmodels.*
import kotlinx.android.synthetic.main.fragment_post.*
import kotlinx.android.synthetic.main.post_buttons.*
import kotlinx.android.synthetic.main.post_comments.*

/**
 * [androidx.fragment.app.Fragment] for showing new posts to the user.
 */
class HomeFragment : PostFragment(), AreaSelectingFragment {
    override val viewModels: List<ViewModel> by lazy { super.viewModels + areaSelectingViewModel }
    override val areaSelectingViewModel by lazyActivityViewModel<AreaSelectingFragmentViewModel>()
    override val viewModel by lazyViewModel<HomeFragmentViewModel>()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        areaSelectingViewModel.preferredAreaName.observe(this) {
            if (it.isNotEmpty()) launchCatching { viewModel.nextPost(it) }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        super.onCreateView(inflater, container, savedInstanceState)
            .apply { findViewById<View>(R.id.buttons).isVisible = true }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        text.setText(R.string.home_empty)
        extinguish.setOnClickListener {
            launchCatching {
                viewModel.spread(false)
                resetHomeView()
            }
        }
        ignite.setOnClickListener {
            launchCatching {
                viewModel.spread(true)
                resetHomeView()
            }
        }
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
                private val mainViewModel by lazyActivityViewModel<MainActivityViewModel>()

                override fun onViewAttachedToWindow(v: View?) = switchItems(true)

                override fun onViewDetachedFromWindow(v: View?) = switchItems(false)

                private fun switchItems(showAreaStuff: Boolean) {
                    menu.forEach { it.isVisible = areaStuff.contains(it.itemId) == showAreaStuff }
                    mainViewModel.setNotificationBadgeVisible(!showAreaStuff)
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

    private fun resetHomeView() {
        content.scrollToPosition(0)
        comments_list.scrollToPosition(0)
        go_up.isVisible = false
        go_down.isVisible = false
    }
}
