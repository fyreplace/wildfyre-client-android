package net.wildfyre.client.data.repositories

import androidx.core.content.edit
import net.wildfyre.client.Constants
import net.wildfyre.client.WildFyreApplication
import net.wildfyre.client.data.Services
import net.wildfyre.client.data.await

object AreaRepository {
    var preferredAreaName: String
        get() = WildFyreApplication.preferences.getString(Constants.Preferences.AREA_PREFERRED, "").orEmpty()
        set(value) = WildFyreApplication.preferences.edit { putString(Constants.Preferences.AREA_PREFERRED, value) }

    suspend fun getAreas() = Services.webService.getAreas(AuthRepository.authToken.value!!).await()

    suspend fun getAreaReputation() =
        Services.webService.getAreaRep(
            AuthRepository.authToken.value!!,
            preferredAreaName
        ).await()
}
