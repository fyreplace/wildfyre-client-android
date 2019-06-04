package net.wildfyre.client.ui.fragments

import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import net.wildfyre.client.R
import net.wildfyre.client.viewmodels.AreaSelectingFragmentViewModel

/**
 * Interface for fragments displaying an area selector in their menu.
 */
interface AreaSelectingFragment {
    val areaSelectingViewModel: AreaSelectingFragmentViewModel

    fun onCreateOptionsMenu(fragment: Fragment, menu: Menu, inflater: MenuInflater) {
        val areaSelectorMenuItem = menu.findItem(R.id.action_area_selector)
        val areaSpinner = areaSelectorMenuItem?.actionView as Spinner

        areaSelectingViewModel.preferredArea.observe(fragment.viewLifecycleOwner, Observer { area ->
            if (area == null) {
                return@Observer
            }

            areaSelectorMenuItem.title =
                fragment.getString(R.string.area_selecting_actions_area_selector, area.displayname)
            areaSelectingViewModel.areas.value
                ?.indexOfFirst { it.name == area.name }
                ?.run { areaSpinner.setSelection(this) }
        })

        val adapter = ArrayAdapter<String>(fragment.context!!, R.layout.actions_area_item)
        areaSpinner.adapter = adapter

        areaSelectingViewModel.preferredArea.value?.let {
            areaSpinner.setSelection(areaSelectingViewModel.areas.value!!.indexOf(it))
        }

        areaSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                areaSelectingViewModel.areas.value?.get(position)?.name?.let {
                    areaSelectingViewModel.setPreferredAreaNameAsync(it)
                    areaSelectingViewModel.updatePreferredAreaAsync()
                }
            }
        }

        areaSelectingViewModel.areas.observe(fragment.viewLifecycleOwner, Observer { areas ->
            adapter.run { clear(); addAll(areas.map { it.displayname }) }

            // If there is no preferred area yet, then this is the first run; set it to the first area that comes
            if (areaSelectingViewModel.preferredAreaName.value == null) {
                areas.firstOrNull()?.name?.let { areaSelectingViewModel.setPreferredAreaNameAsync(it) }
            }
        })

        areaSelectingViewModel.updateAreasAsync()
    }
}
