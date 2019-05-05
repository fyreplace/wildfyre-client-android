package net.wildfyre.client.views

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.annotation.MenuRes
import androidx.core.view.forEach
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import net.wildfyre.client.R
import net.wildfyre.client.databinding.ActionsAreaReputationBinding
import net.wildfyre.client.databinding.ActionsAreaSpreadBinding
import net.wildfyre.client.viewmodels.AreaSelectingFragmentViewModel
import net.wildfyre.client.viewmodels.MainActivityViewModel

/**
 * Base class for [androidx.fragment.app.Fragment]s that depend on a selected area.
 */
abstract class AreaSelectingFragment(contentLayoutId: Int, @MenuRes private val menuResource: Int) :
    FailureHandlingFragment(contentLayoutId) {
    protected lateinit var areaSelectingViewModel: AreaSelectingFragmentViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        areaSelectingViewModel = ViewModelProviders.of(activity!!).get(AreaSelectingFragmentViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        activity?.title = ""
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(menuResource, menu)

        ActionsAreaSpreadBinding.bind(menu.findItem(R.id.action_area_spread).actionView).apply {
            lifecycleOwner = viewLifecycleOwner
            model = areaSelectingViewModel
        }

        ActionsAreaReputationBinding.bind(menu.findItem(R.id.action_area_reputation).actionView).apply {
            lifecycleOwner = viewLifecycleOwner
            model = areaSelectingViewModel
        }

        val areaSelectorMenuItem = menu.findItem(R.id.action_area_selector)
        val areaSpinner = areaSelectorMenuItem?.actionView as Spinner

        /*
         Every time the area selector is expanded ot collapsed, different menu items should be shown.
         When the selector is collapsed, regular actions such as the subscribe button are available; when it is expanded
         however, the user's spread and reputation for the selected area should be shown instead.
         */
        areaSelectorMenuItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            private val areaStuff = listOf(R.id.action_area_spread, R.id.action_area_reputation)

            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                switchItems(true)
                ViewModelProviders.of(activity!!).get(MainActivityViewModel::class.java)
                    .setNotificationBadgeVisible(false)
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                switchItems(false)
                ViewModelProviders.of(activity!!).get(MainActivityViewModel::class.java)
                    .setNotificationBadgeVisible(true)
                return true
            }

            private fun switchItems(showAreaStuff: Boolean) {
                menu.forEach { it.isVisible = areaStuff.contains(it.itemId) == showAreaStuff }
            }
        })

        areaSelectingViewModel.preferredArea.observe(viewLifecycleOwner, Observer { area ->
            if (area == null) {
                return@Observer
            }

            areaSelectorMenuItem.title = getString(R.string.main_actions_area_selector, area.displayname)
            areaSelectingViewModel.areas.value
                ?.indexOfFirst { it.name == area.name }
                ?.run { areaSpinner.setSelection(this) }
        })

        val adapter = ArrayAdapter<String>(context!!, R.layout.actions_area_item)
        areaSpinner.adapter = adapter

        areaSelectingViewModel.preferredArea.value?.let {
            areaSpinner.setSelection(areaSelectingViewModel.areas.value!!.indexOf(it))
        }

        areaSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                areaSelectingViewModel.setPreferredAreaName(areaSelectingViewModel.areas.value!![position].name!!)
                areaSelectingViewModel.updatePreferredArea()
            }
        }

        areaSelectingViewModel.areas.observe(viewLifecycleOwner, Observer { areas ->
            adapter.run { clear(); addAll(areas.map { it.displayname }) }

            // If there is no preferred area yet, then this is the first run; set it to the first area that comes
            if (areaSelectingViewModel.preferredAreaName.value == null) {
                areaSelectingViewModel.setPreferredAreaName(areas.first().name!!)
            }
        })

        areaSelectingViewModel.updateAreas()
    }
}