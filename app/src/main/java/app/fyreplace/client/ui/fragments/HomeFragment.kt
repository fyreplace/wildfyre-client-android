package app.fyreplace.client.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.core.view.forEach
import androidx.core.view.isVisible
import androidx.lifecycle.observe
import app.fyreplace.client.R
import app.fyreplace.client.databinding.ActionAreaSelectingAreaReputationBinding
import app.fyreplace.client.databinding.ActionAreaSelectingAreaSpreadBinding
import app.fyreplace.client.viewmodels.AreaSelectingFragmentViewModel
import app.fyreplace.client.viewmodels.HomeFragmentViewModel
import app.fyreplace.client.viewmodels.MainActivityViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * [androidx.fragment.app.Fragment] for showing new posts to the user.
 */
class HomeFragment : PostFragment(), AreaSelectingFragment {
    override val viewModel by viewModel<HomeFragmentViewModel>()
    private val areaSelectingViewModel by sharedViewModel<AreaSelectingFragmentViewModel>()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        areaSelectingViewModel.preferredAreaName.observe(this) {
            if (it.isNotEmpty()) launch { viewModel.nextPost(it) }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = super.onCreateView(inflater, container, savedInstanceState)
        .apply { findViewById<View>(R.id.buttons).isVisible = true }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bd.text.setText(R.string.home_empty)
        bd.buttons.extinguish.setOnClickListener { launch { viewModel.spread(false); resetHomeView() } }
        bd.buttons.ignite.setOnClickListener { launch { viewModel.spread(true); resetHomeView() } }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.actions_fragment_home, menu)
        onCreateOptionsMenu(this, menu, inflater)

        ActionAreaSelectingAreaSpreadBinding.bind(menu.findItem(R.id.action_area_spread).actionView)
            .run {
                lifecycleOwner = viewLifecycleOwner
                model = areaSelectingViewModel
            }

        ActionAreaSelectingAreaReputationBinding.bind(menu.findItem(R.id.action_area_reputation).actionView)
            .run {
                lifecycleOwner = viewLifecycleOwner
                model = areaSelectingViewModel
            }

        menu.findItem(R.id.action_area_selector)?.actionView
            ?.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
                private val areaStuff = listOf(R.id.action_area_spread, R.id.action_area_reputation)
                private val mainViewModel by sharedViewModel<MainActivityViewModel>()

                override fun onViewAttachedToWindow(v: View?) = switchItems(true)

                override fun onViewDetachedFromWindow(v: View?) = switchItems(false)

                private fun switchItems(showAreaStuff: Boolean) {
                    mainViewModel.setNotificationBadgeVisible(!showAreaStuff)
                    menu.forEach {
                        it.isVisible = it.itemId != R.id.action_delete
                            && areaStuff.contains(it.itemId) == showAreaStuff
                    }
                }
            })

        super<PostFragment>.onCreateOptionsMenu(menu, inflater)
        val showAsAction =
            if (resources.getBoolean(R.bool.home_show_full_menu))
                MenuItem.SHOW_AS_ACTION_IF_ROOM
            else
                MenuItem.SHOW_AS_ACTION_NEVER

        for (id in setOf(R.id.action_area_selector, R.id.action_share, R.id.action_delete)) {
            menu.findItem(id)
                .setShowAsAction(showAsAction or MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW)
        }
    }

    override fun canUseFragmentArgs() = false

    private fun resetHomeView() {
        bd.content.scrollToPosition(0)
        cbd.commentsList.scrollToPosition(0)
        cbd.goUp.isVisible = false
        cbd.goDown.isVisible = false
    }
}
