package net.wildfyre.client.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import net.wildfyre.client.data.Area
import net.wildfyre.client.data.AreaRepository

class HomeFragmentViewModel(application: Application) : FailureHandlingViewModel(application) {
    private val _preferredArea = MediatorLiveData<Area?>()

    val areas = AreaRepository.areas
    val preferredAreaName = AreaRepository.preferredAreaName
    val preferredArea: LiveData<Area?>
        get() = _preferredArea
    val currentAreaSpread: LiveData<Long> = Transformations.map(preferredArea) { it?.spread ?: 0 }
    val currentAreaReputation: LiveData<Long> = Transformations.map(preferredArea) { it?.rep ?: 0 }

    init {
        _preferredArea.addSource(areas) {
            _preferredArea.value = it?.firstOrNull { area -> area.name == preferredAreaName.value }
        }

        _preferredArea.addSource(preferredAreaName) {
            _preferredArea.value = areas.value?.firstOrNull { area -> area.name == it }
        }
    }

    fun updateAreas() {
        AreaRepository.fetchAreas(this)
    }

    val setPreferredAreaName = AreaRepository::setPreferredAreaName

    fun setPreferredAreaDisplayName(displayName: String) {
        areas.value?.firstOrNull { it.displayname == displayName }?.name?.let {
            setPreferredAreaName(it)
        }
    }
}