package app.fyreplace.client.viewmodels

import androidx.lifecycle.*
import app.fyreplace.client.data.models.Area
import app.fyreplace.client.data.models.Reputation
import app.fyreplace.client.data.repositories.AreaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AreaSelectingFragmentViewModel : ViewModel() {
    private val mAreas = MutableLiveData<List<Area>>()
    private val mPreferredAreaName = MutableLiveData<String>()
    private val mPreferredArea = MediatorLiveData<Area?>()
    private val mPreferredAreaIndex = MediatorLiveData<Int>()
    private val mPreferredAreaReputationInfo = MutableLiveData<Reputation>()

    val areas: LiveData<List<Area>> = mAreas
    val areasDisplayNames: LiveData<List<String>> = areas.map { it.map { a -> a.displayname } }
    val preferredAreaName: LiveData<String> = mPreferredAreaName
    val preferredArea: LiveData<Area?> = mPreferredArea
    val preferredAreaIndex: LiveData<Int> = mPreferredAreaIndex
    val currentAreaSpread: LiveData<Int> = mPreferredAreaReputationInfo.map { it.spread }
    val currentAreaReputation: LiveData<Int> = mPreferredAreaReputationInfo.map { it.reputation }

    init {
        mPreferredArea.addSource(areas) { updatePreferredArea(it, preferredAreaName.value) }
        mPreferredArea.addSource(preferredAreaName) { updatePreferredArea(areas.value, it) }
        mPreferredAreaIndex.addSource(areas) { updatePreferredAreaIndex(it, preferredAreaName.value) }
        mPreferredAreaIndex.addSource(preferredAreaName) { updatePreferredAreaIndex(areas.value, it) }
    }

    suspend fun updateAreas() {
        val fetchedAreas = withContext(Dispatchers.IO) { AreaRepository.getAreas() }
        mAreas.postValue(fetchedAreas)
        val current = preferredAreaName.value

        if (current == null || current.isEmpty()) {
            val name = AreaRepository.preferredAreaName
            updatePreferredAreaInfo(if (name.isNotEmpty()) name else fetchedAreas.first().name)
        }
    }

    suspend fun setPreferredAreaName(areaName: String) {
        if (areaName != AreaRepository.preferredAreaName) {
            updatePreferredAreaInfo(areaName)
        }
    }

    private suspend fun updatePreferredAreaInfo(areaName: String) {
        AreaRepository.preferredAreaName = areaName
        mPreferredAreaName.postValue(areaName)
        mPreferredAreaReputationInfo.postValue(withContext(Dispatchers.IO) { AreaRepository.getAreaReputation() })
    }

    private fun updatePreferredArea(areas: List<Area>?, areaName: String?) {
        if (preferredArea.value?.name != areaName) {
            areas?.firstOrNull { it.name == areaName }?.let { mPreferredArea.postValue(it) }
        }
    }

    private fun updatePreferredAreaIndex(areas: List<Area>?, areaName: String?) =
        mPreferredAreaIndex.postValue(areas?.indexOfFirst { it.name == areaName } ?: -1)
}
