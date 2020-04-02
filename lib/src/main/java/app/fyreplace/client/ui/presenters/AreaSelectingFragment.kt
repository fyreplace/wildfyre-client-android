package app.fyreplace.client.ui.presenters

import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.Menu
import android.view.MenuInflater
import androidx.core.text.set
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import app.fyreplace.client.data.models.Area
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
        val viewModel = fragment.getSharedViewModel<AreaSelectingFragmentViewModel>()
        val areaSelectorMenuItem = menu.findItem(R.id.action_area_selector)
        val bd = ActionAreaSelectingSelectorBinding
            .bind(areaSelectorMenuItem.actionView)
            .apply {
                lifecycleOwner = fragment.viewLifecycleOwner
                model = viewModel
            }

        viewModel.preferredArea.observe(fragment.viewLifecycleOwner) {
            areaSelectorMenuItem.title = fragment.getString(
                R.string.area_selecting_action_selector,
                it.displayName
            )
        }

        viewModel.areas.observe(fragment.viewLifecycleOwner) { areas ->
            bd.button.setOnClickListener {
                MaterialAlertDialogBuilder(fragment.requireContext())
                    .setTitle(R.string.area_selecting_action_selector_dialog_title)
                    .setItems(areas.map { area ->
                        if (viewModel.preferredArea.value == area) area.selectedDisplayName
                        else area.displayName
                    }.toTypedArray()) { _, i -> viewModel.setPreferredAreaName(areas[i].name) }
                    .show()
            }
        }

        areaSelectorMenuItem.setOnMenuItemClickListener { bd.button.callOnClick() }
        launch { viewModel.updateAreas() }
    }

    private val Area.selectedDisplayName
        get() = getContext()?.run {
            SpannableString(
                getString(
                    R.string.area_selecting_action_selector_dialog_current,
                    displayName
                )
            ).apply { set(0..length, ForegroundColorSpan(getColor(R.color.inactive))) }
        }
}
