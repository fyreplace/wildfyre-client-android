package app.fyreplace.client.ui.presenters

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.core.view.isVisible
import androidx.lifecycle.observe
import app.fyreplace.client.app.home.R
import app.fyreplace.client.viewmodels.AreaSelectingFragmentViewModel
import app.fyreplace.client.viewmodels.HomeFragmentViewModel
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
    ) = super.onCreateView(inflater, container, savedInstanceState).apply {
        bd.button?.isVisible = !resources.getBoolean(R.bool.home_show_full_menu)
        bd.buttons.extinguish.isVisible = true
        bd.buttons.ignite.isVisible = true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bd.text.setText(R.string.home_empty)
        bd.button?.setText(R.string.home_change_area)
        bd.buttons.extinguish.setOnClickListener { launch { viewModel.spread(false); resetHomeView() } }
        bd.buttons.ignite.setOnClickListener { launch { viewModel.spread(true); resetHomeView() } }

        with(bd.refresher) {
            isEnabled = true
            setColorSchemeResources(R.color.secondary)
            setProgressBackgroundColorSchemeResource(R.color.background)
            setOnRefreshListener {
                launch {
                    viewModel.nextPost()
                    isRefreshing = false
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.actions_fragment_area_selecting, menu)
        onCreateOptionsMenu(this, menu, inflater)
        super<PostFragment>.onCreateOptionsMenu(menu, inflater)
        val hiddenItems = setOf(
            R.id.action_area_selector,
            R.id.action_share,
            R.id.action_delete,
            R.id.action_flag
        )

        val showAsAction =
            if (resources.getBoolean(R.bool.home_show_full_menu)) MenuItem.SHOW_AS_ACTION_IF_ROOM
            else MenuItem.SHOW_AS_ACTION_NEVER

        for (id in hiddenItems) {
            menu.findItem(id).setShowAsAction(showAsAction)
        }

        bd.button?.setOnClickListener {
            menu.findItem(hiddenItems.first()).actionView.findViewById<View>(R.id.button)
                .callOnClick()
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
