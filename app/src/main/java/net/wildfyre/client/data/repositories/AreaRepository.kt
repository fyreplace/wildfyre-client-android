package net.wildfyre.client.data.repositories

import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import net.wildfyre.client.Constants
import net.wildfyre.client.WildFyreApplication
import net.wildfyre.client.data.Area
import net.wildfyre.client.data.Reputation
import net.wildfyre.client.data.Services
import net.wildfyre.client.data.await

object AreaRepository {
    private val mutableAreas = MutableLiveData<List<Area>>()
    private val mutablePreferredAreaName = MutableLiveData<String?>()
    private val mutablePreferredAreaReputation = MutableLiveData<Reputation>()

    val areas: LiveData<List<Area>> = mutableAreas
    val preferredAreaName: LiveData<String?> = mutablePreferredAreaName
    val preferredAreaReputation: LiveData<Reputation> = mutablePreferredAreaReputation

    init {
        mutablePreferredAreaName.value = WildFyreApplication.preferences.getString(
            Constants.Preferences.AREA_PREFERRED,
            null
        )
    }

    suspend fun fetchAreas() =
        mutableAreas.postValue(Services.webService.getAreas(AuthRepository.authToken.value!!).await())

    suspend fun fetchAreaReputation(areaName: String) =
        mutablePreferredAreaReputation.postValue(
            Services.webService.getAreaRep(
                AuthRepository.authToken.value!!,
                areaName
            ).await()
        )

    fun setPreferredAreaName(name: String) {
        if (name != preferredAreaName.value) {
            WildFyreApplication.preferences.edit { putString(Constants.Preferences.AREA_PREFERRED, name) }
            mutablePreferredAreaName.postValue(name)
        }
    }
}
