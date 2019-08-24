package app.fyreplace.client.viewmodels

import androidx.lifecycle.*
import app.fyreplace.client.data.models.Area
import app.fyreplace.client.data.models.Reputation
import app.fyreplace.client.data.repositories.AreaRepository

class AreaSelectingFragmentViewModel : ViewModel() {
    private val mAreas = MutableLiveData<List<Area>>()
    private val mPreferredAreaName = MutableLiveData<String>()
    private val mPreferredArea = MediatorLiveData<Area?>()
    private val mPreferredAreaReputationInfo = MutableLiveData<Reputation>()

    val areas: LiveData<List<Area>> = mAreas.distinctUntilChanged()
    val areasDisplayNames: LiveData<List<String>> = areas.map { it.map { a -> a.displayName } }
    val preferredAreaName: LiveData<String> = mPreferredAreaName.distinctUntilChanged()
    val preferredArea: LiveData<Area?> = mPreferredArea.distinctUntilChanged()
    val preferredAreaIndex: LiveData<Int?> = areas.map { areas ->
        areas.indexOfFirst { it.name == AreaRepository.preferredAreaName }.takeIf { it > -1 }
    }.distinctUntilChanged()
    val currentAreaSpread: LiveData<Int> = mPreferredAreaReputationInfo.map { it.spread }
    val currentAreaReputation: LiveData<Int> = mPreferredAreaReputationInfo.map { it.reputation }

    init {
        mPreferredArea.addSource(areas) { updatePreferredArea(it, preferredAreaName.value) }
        mPreferredArea.addSource(preferredAreaName) { updatePreferredArea(areas.value, it) }
    }

    suspend fun updateAreas() {
        val fetchedAreas = AreaRepository.getAreas()
        mAreas.postValue(fetchedAreas)

        if (preferredAreaName.value.isNullOrEmpty()) {
            val name = AreaRepository.preferredAreaName
            updatePreferredAreaInfo(if (name.isNotEmpty()) name else fetchedAreas.first().name)
        }
    }

    suspend fun setPreferredAreaName(areaName: String) {
        if (areaName != AreaRepository.preferredAreaName && preferredAreaIndex.value != null) {
            updatePreferredAreaInfo(areaName)
        }
    }

    private suspend fun updatePreferredAreaInfo(areaName: String) {
        AreaRepository.preferredAreaName = areaName
        mPreferredAreaName.postValue(areaName)
        mPreferredAreaReputationInfo.postValue(AreaRepository.getAreaReputation())
    }

    private fun updatePreferredArea(areas: List<Area>?, areaName: String?) =
        areas?.firstOrNull { it.name == areaName }?.let { mPreferredArea.postValue(it) }
}
