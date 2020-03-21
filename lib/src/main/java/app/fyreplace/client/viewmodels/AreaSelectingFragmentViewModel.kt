package app.fyreplace.client.viewmodels

import androidx.lifecycle.*
import app.fyreplace.client.data.models.Area
import app.fyreplace.client.data.models.Reputation
import app.fyreplace.client.data.repositories.AreaRepository

class AreaSelectingFragmentViewModel(private val areaRepository: AreaRepository) : ViewModel() {
    private val mAreas = MutableLiveData<List<Area>>()
    private val mPreferredAreaName = MutableLiveData<String>()
    private val mPreferredArea = MediatorLiveData<Area>()
    private val mPreferredAreaIndex = MutableLiveData<Int>()
    private val mPreferredAreaReputationInfo = MutableLiveData<Reputation>()

    val areas: LiveData<List<Area>> = mAreas
    val areasDisplayNames: LiveData<List<String>> = areas.map { it.map { a -> a.displayName } }
    val preferredAreaName: LiveData<String> = mPreferredAreaName.distinctUntilChanged()
    val preferredArea: LiveData<Area> = mPreferredArea.distinctUntilChanged()
    val preferredAreaIndex: LiveData<Int> = mPreferredAreaIndex
    val currentAreaSpread: LiveData<Int> = mPreferredAreaReputationInfo.map { it.spread }
    val currentAreaReputation: LiveData<Int> = mPreferredAreaReputationInfo.map { it.reputation }

    init {
        mPreferredArea.addSource(areas) { updatePreferredArea(it, preferredAreaName.value) }
        mPreferredArea.addSource(preferredAreaName) { updatePreferredArea(areas.value, it) }
    }

    suspend fun updateAreas() {
        val fetchedAreas = areaRepository.getAreas()
        val repoAreaName = areaRepository.preferredAreaName
        mAreas.postValue(fetchedAreas)

        if (preferredAreaName.value.isNullOrEmpty()) {
            updatePreferredAreaInfo(if (repoAreaName.isNotEmpty()) repoAreaName else fetchedAreas.first().name)
        }
    }

    suspend fun setPreferredAreaName(areaName: String) {
        if (areaName != areaRepository.preferredAreaName) {
            updatePreferredAreaInfo(areaName)
        }
    }

    private suspend fun updatePreferredAreaInfo(areaName: String) {
        areaRepository.preferredAreaName = areaName
        mPreferredAreaName.postValue(areaName)
        mPreferredAreaReputationInfo.postValue(
            if (areaName.isNotBlank()) areaRepository.getAreaReputation()
            else Reputation(0, 0)
        )
    }

    private fun updatePreferredArea(areas: List<Area>?, areaName: String?) =
        areas?.withIndex()?.firstOrNull { it.value.name == areaName }?.let {
            mPreferredArea.postValue(it.value)
            mPreferredAreaIndex.postValue(it.index)
        }
}
