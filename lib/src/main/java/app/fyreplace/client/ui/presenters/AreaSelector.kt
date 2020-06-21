package app.fyreplace.client.ui.presenters

import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.Menu
import androidx.core.content.ContextCompat
import androidx.core.text.set
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import app.fyreplace.client.data.models.Area
import app.fyreplace.client.lib.R
import app.fyreplace.client.lib.databinding.ActionAreaSelectorSelectorBinding
import app.fyreplace.client.ui.FailureHandler
import app.fyreplace.client.viewmodels.AreaSelectorViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class AreaSelector<F>(private val fragment: F) where F : Fragment, F : FailureHandler {
    val viewModel by fragment.sharedViewModel<AreaSelectorViewModel>()
    var showAreaSelector: () -> Unit = {}

    fun onCreateOptionsMenu(menu: Menu) {
        val areaSelectorAction = menu.findItem(R.id.action_area_selector)
        areaSelectorAction.setOnMenuItemClickListener { showAreaSelector(); true }

        ActionAreaSelectorSelectorBinding
            .bind(areaSelectorAction.actionView)
            .apply {
                lifecycleOwner = fragment.viewLifecycleOwner
                model = viewModel
                button.setOnClickListener { showAreaSelector() }
            }

        viewModel.preferredArea.observe(fragment.viewLifecycleOwner) {
            areaSelectorAction.title = fragment.getString(
                R.string.area_selector_action_selector,
                it.displayName
            )
        }

        viewModel.areas.observe(fragment.viewLifecycleOwner) { areas ->
            showAreaSelector = {
                MaterialAlertDialogBuilder(fragment.requireContext())
                    .setTitle(R.string.area_selector_action_selector_dialog_title)
                    .setItems(areas.map { area ->
                        if (viewModel.preferredArea.value == area) area.selectedDisplayName
                        else area.displayName
                    }.toTypedArray()) { _, i -> viewModel.setPreferredAreaName(areas[i].name) }
                    .show()
            }
        }

        fragment.launch { viewModel.updateAreas() }
    }

    private val Area.selectedDisplayName
        get() = fragment.context?.run {
            SpannableString(
                getString(
                    R.string.area_selector_action_selector_dialog_current,
                    displayName
                )
            ).apply {
                val color = ContextCompat.getColor(this@run, R.color.inactive)
                set(0..length, ForegroundColorSpan(color))
            }
        }
}
