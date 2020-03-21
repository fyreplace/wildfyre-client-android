package app.fyreplace.client.viewmodels

import androidx.lifecycle.*
import app.fyreplace.client.data.models.Area
import app.fyreplace.client.data.repositories.AreaRepository

class AreaSelectingFragmentViewModel(private val areaRepository: AreaRepository) : ViewModel() {
    private val mAreas = MutableLiveData<List<Area>>()
    private val mPreferredAreaName = MutableLiveData<String>()
    private val mPreferredArea = MediatorLiveData<Area>()

    val areas: LiveData<List<Area>> = mAreas
    val preferredAreaName: LiveData<String> = mPreferredAreaName.distinctUntilChanged()
    val preferredArea: LiveData<Area> = mPreferredArea.distinctUntilChanged()

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

    fun setPreferredAreaName(areaName: String) {
        if (areaName != areaRepository.preferredAreaName) {
            updatePreferredAreaInfo(areaName)
        }
    }

    private fun updatePreferredAreaInfo(areaName: String) {
        areaRepository.preferredAreaName = areaName
        mPreferredAreaName.postValue(areaName)
    }

    private fun updatePreferredArea(areas: List<Area>?, areaName: String?) =
        areas?.withIndex()?.firstOrNull { it.value.name == areaName }?.let {
            mPreferredArea.postValue(it.value)
        }
}
