package app.fyreplace.client.ui.presenters

import android.view.Menu
import android.view.MenuInflater
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import app.fyreplace.client.lib.R
import app.fyreplace.client.lib.databinding.ActionAreaSelectingSelectorBinding
import app.fyreplace.client.ui.FailureHandler
import app.fyreplace.client.viewmodels.AreaSelectingFragmentViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.getSharedViewModel

/**
 * Interface for fragments displaying an area selector in their menu.
 */
interface AreaSelectingFragment : FailureHandler {
    fun onCreateOptionsMenu(fragment: Fragment, menu: Menu, inflater: MenuInflater) {
        val areaSelectingViewModel = fragment.getSharedViewModel<AreaSelectingFragmentViewModel>()
        val areaSelectorMenuItem = menu.findItem(R.id.action_area_selector)
        val bd = ActionAreaSelectingSelectorBinding
            .bind(areaSelectorMenuItem.actionView)
            .apply {
                lifecycleOwner = fragment.viewLifecycleOwner
                model = areaSelectingViewModel
            }

        areaSelectingViewModel.preferredArea.observe(fragment.viewLifecycleOwner) {
            areaSelectorMenuItem.title = fragment.getString(
                R.string.area_selecting_action_selector,
                it.displayName
            )
        }

        areaSelectingViewModel.areas.observe(fragment.viewLifecycleOwner) { areas ->
            bd.button.setOnClickListener {
                MaterialAlertDialogBuilder(fragment.requireContext())
                    .setTitle(R.string.area_selecting_action_selector_dialog_title)
                    .setItems(areas.map { it.displayName }.toTypedArray()) { _, i ->
                        areaSelectingViewModel.setPreferredAreaName(areas[i].name)
                    }
                    .show()
            }
        }

        areaSelectorMenuItem.setOnMenuItemClickListener { bd.button.callOnClick() }
        launch { areaSelectingViewModel.updateAreas() }
    }
}
