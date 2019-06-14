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
    private val mAreas = MutableLiveData<List<Area>>()
    private val mPreferredAreaName = MutableLiveData<String>()
    private val mPreferredArea = MediatorLiveData<Area?>()
    private val mPreferredAreaIndex = MediatorLiveData<Int>()
    private val mPreferredAreaReputationInfo = MutableLiveData<Reputation>()

    val areas: LiveData<List<Area>> = mAreas
    val areasDisplayNames: LiveData<List<String>> = Transformations.map(areas) { it.map { a -> a.displayname } }
    val preferredAreaName: LiveData<String> = mPreferredAreaName
    val preferredArea: LiveData<Area?> = mPreferredArea
    val preferredAreaIndex: LiveData<Int> = mPreferredAreaIndex
    val currentAreaSpread: LiveData<Int> =
        Transformations.map(mPreferredAreaReputationInfo) { it.spread }
    val currentAreaReputation: LiveData<Int> =
        Transformations.map(mPreferredAreaReputationInfo) { it.reputation }

    init {
        mPreferredArea.addSource(areas) { updatePreferredArea(it, preferredAreaName.value) }
        mPreferredArea.addSource(preferredAreaName) { updatePreferredArea(areas.value, it) }
        mPreferredAreaIndex.addSource(areas) { updatePreferredAreaIndex(it, preferredAreaName.value) }
        mPreferredAreaIndex.addSource(preferredAreaName) { updatePreferredAreaIndex(areas.value, it) }
    }

    fun updateAreasAsync() = launchCatching {
        val fetchedAreas = withContext(Dispatchers.IO) { AreaRepository.getAreas() }
        mAreas.postValue(fetchedAreas)
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
