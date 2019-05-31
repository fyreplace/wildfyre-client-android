package net.wildfyre.client.data.repositories

import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import net.wildfyre.client.Constants
import net.wildfyre.client.WildFyreApplication
import net.wildfyre.client.data.*

object AreaRepository {
    private val mutableAreas = MutableLiveData<List<Area>>()
    private val mutablePreferredAreaName = MutableLiveData<String>()
    private val mutablePreferredAreaReputation =
        MutableLiveData<Reputation>()

    val areas: LiveData<List<Area>> =
        mutableAreas
    val preferredAreaName: LiveData<String> =
        mutablePreferredAreaName
    val preferredAreaReputation: LiveData<Reputation> =
        mutablePreferredAreaReputation

    init {
        WildFyreApplication.preferences.getString(Constants.Preferences.AREA_PREFERRED, null)
            ?.let { mutablePreferredAreaName.value = it }
    }

    fun fetchAreas(fh: FailureHandler) =
        Services.webService.getAreas(AuthRepository.authToken.value!!)
            .then(fh) { mutableAreas.value = it }

    fun fetchAreaReputation(fh: FailureHandler, areaName: String) =
        Services.webService.getAreaRep(AuthRepository.authToken.value!!, areaName)
            .then(fh) { mutablePreferredAreaReputation.value = it }

    fun setPreferredAreaName(name: String) {
        if (name != preferredAreaName.value) {
            WildFyreApplication.preferences.edit { putString(Constants.Preferences.AREA_PREFERRED, name) }
            mutablePreferredAreaName.value = name
        }
    }
}
