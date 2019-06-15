package app.fyreplace.client.data.repositories

import androidx.core.content.edit
import app.fyreplace.client.Constants
import app.fyreplace.client.FyreplaceApplication
import app.fyreplace.client.data.Services
import app.fyreplace.client.data.await

object AreaRepository {
    var preferredAreaName: String
        get() = FyreplaceApplication.preferences.getString(Constants.Preferences.AREA_PREFERRED, "").orEmpty()
        set(value) = FyreplaceApplication.preferences.edit { putString(Constants.Preferences.AREA_PREFERRED, value) }

    suspend fun getAreas() = Services.webService.getAreas(AuthRepository.authToken).await()

    suspend fun getAreaReputation() =
        Services.webService.getAreaRep(
            AuthRepository.authToken,
            preferredAreaName
        ).await()
}
