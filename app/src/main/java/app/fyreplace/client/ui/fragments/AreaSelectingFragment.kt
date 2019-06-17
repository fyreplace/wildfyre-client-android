package app.fyreplace.client.ui.fragments

import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import app.fyreplace.client.R
import app.fyreplace.client.data.FailureHandler
import app.fyreplace.client.databinding.AreaSelectingSpinnerBinding
import app.fyreplace.client.viewmodels.AreaSelectingFragmentViewModel

/**
 * Interface for fragments displaying an area selector in their menu.
 */
interface AreaSelectingFragment : FailureHandler {
    val areaSelectingViewModel: AreaSelectingFragmentViewModel

    fun onCreateOptionsMenu(fragment: Fragment, menu: Menu, inflater: MenuInflater) {
        val areaSelectorMenuItem = menu.findItem(R.id.action_area_selector)
        val areaSpinner = areaSelectorMenuItem?.actionView as Spinner

        AreaSelectingSpinnerBinding.bind(areaSpinner).apply {
            lifecycleOwner = fragment.viewLifecycleOwner
            model = areaSelectingViewModel
        }

        areaSelectingViewModel.preferredArea.observe(fragment.viewLifecycleOwner, Observer {
            if (it != null) {
                areaSelectorMenuItem.title = fragment.getString(
                    R.string.area_selecting_actions_area_selector,
                    it.displayname
                )
            }
        })

        areaSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) = Unit

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                areaSelectingViewModel.areas.value?.get(position)?.name?.let {
                    launchCatching { areaSelectingViewModel.setPreferredAreaName(it) }
                }
            }
        }

        launchCatching { areaSelectingViewModel.updateAreas() }
    }
}
