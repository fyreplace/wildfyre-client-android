package app.fyreplace.client.data.repositories

import android.content.SharedPreferences
import androidx.core.content.edit
import app.fyreplace.client.Constants
import app.fyreplace.client.data.services.WildFyreService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AreaRepository(
    private val wildFyre: WildFyreService,
    private val preferences: SharedPreferences,
    private val auth: AuthRepository
) {
    var preferredAreaName: String
        get() = preferences.getString(Constants.Preferences.AREA_PREFERRED, "").orEmpty()
        set(value) = preferences.edit { putString(Constants.Preferences.AREA_PREFERRED, value) }

    suspend fun getAreas() = withContext(Dispatchers.IO) {
        wildFyre.getAreas(auth.authToken)
    }

    suspend fun getAreaReputation() = withContext(Dispatchers.IO) {
        wildFyre.getAreaRep(auth.authToken, preferredAreaName)
    }
}
