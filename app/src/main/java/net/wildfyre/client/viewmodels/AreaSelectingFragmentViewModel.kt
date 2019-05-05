package net.wildfyre.client.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import net.wildfyre.client.data.Area
import net.wildfyre.client.data.AreaRepository

class AreaSelectingFragmentViewModel(application: Application) : FailureHandlingViewModel(application) {
    private val _preferredArea = MediatorLiveData<Area?>()

    val areas: LiveData<List<Area>> = AreaRepository.areas
    val preferredAreaName: LiveData<String> = AreaRepository.preferredAreaName
    val preferredArea: LiveData<Area?> = _preferredArea
    val currentAreaSpread: LiveData<Long> =
        Transformations.map(AreaRepository.preferredAreaReputation) { it.spread ?: 0 }
    val currentAreaReputation: LiveData<Long> =
        Transformations.map(AreaRepository.preferredAreaReputation) { it.reputation ?: 0 }

    init {
        _preferredArea.addSource(areas) {
            _preferredArea.value = it?.firstOrNull { area -> area.name == preferredAreaName.value }
        }

        _preferredArea.addSource(preferredAreaName) {
            _preferredArea.value = areas.value?.firstOrNull { area -> area.name == it }
        }
    }

    fun updateAreas() = AreaRepository.fetchAreas(this)

    fun updatePreferredArea() = AreaRepository.fetchAreaReputation(this, preferredAreaName.value!!)

    fun setPreferredAreaName(areaName: String) = AreaRepository.setPreferredAreaName(areaName)
}