package net.wildfyre.client.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.wildfyre.client.data.models.Area
import net.wildfyre.client.data.models.Reputation
import net.wildfyre.client.data.repositories.AreaRepository

class AreaSelectingFragmentViewModel(application: Application) : FailureHandlingViewModel(application) {
    private val _areas = MutableLiveData<List<Area>>()
    private val _preferredAreaName = MutableLiveData<String>()
    private val _preferredArea = MediatorLiveData<Area?>()
    private val _preferredAreaIndex = MediatorLiveData<Int>()
    private val _preferredAreaReputationInfo = MutableLiveData<Reputation>()

    val areas: LiveData<List<Area>> = _areas
    val areasDisplayNames: LiveData<List<String>> = Transformations.map(areas) { it.map { a -> a.displayname } }
    val preferredAreaName: LiveData<String> = _preferredAreaName
    val preferredArea: LiveData<Area?> = _preferredArea
    val preferredAreaIndex: LiveData<Int> = _preferredAreaIndex
    val currentAreaSpread: LiveData<Int> =
        Transformations.map(_preferredAreaReputationInfo) { it.spread }
    val currentAreaReputation: LiveData<Int> =
        Transformations.map(_preferredAreaReputationInfo) { it.reputation }

    init {
        _preferredArea.addSource(areas) { updatePreferredArea(it, preferredAreaName.value) }
        _preferredArea.addSource(preferredAreaName) { updatePreferredArea(areas.value, it) }
        _preferredAreaIndex.addSource(areas) { updatePreferredAreaIndex(it, preferredAreaName.value) }
        _preferredAreaIndex.addSource(preferredAreaName) { updatePreferredAreaIndex(areas.value, it) }
    }

    fun updateAreasAsync() = launchCatching {
        val fetchedAreas = withContext(Dispatchers.IO) { AreaRepository.getAreas() }
        _areas.postValue(fetchedAreas)
        val current = preferredAreaName.value

        if (current == null || current.isEmpty()) {
            val name = AreaRepository.preferredAreaName
            updatePreferredAreaInfo(if (name.isNotEmpty()) name else fetchedAreas.first().name)
        }
    }

    fun setPreferredAreaNameAsync(areaName: String) = launchCatching {
        if (areaName != AreaRepository.preferredAreaName) {
            updatePreferredAreaInfo(areaName)
        }
    }

    private suspend fun updatePreferredAreaInfo(areaName: String) {
        AreaRepository.preferredAreaName = areaName
        _preferredAreaName.postValue(areaName)
        _preferredAreaReputationInfo.postValue(withContext(Dispatchers.IO) { AreaRepository.getAreaReputation() })
    }

    private fun updatePreferredArea(areas: List<Area>?, areaName: String?) {
        if (preferredArea.value?.name != areaName) {
            areas?.firstOrNull { it.name == areaName }?.let { _preferredArea.postValue(it) }
        }
    }

    private fun updatePreferredAreaIndex(areas: List<Area>?, areaName: String?) =
        _preferredAreaIndex.postValue(areas?.indexOfFirst { it.name == areaName } ?: -1)
}
