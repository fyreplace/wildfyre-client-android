package app.fyreplace.client.data.repositories

import android.content.SharedPreferences
import androidx.core.content.edit
import app.fyreplace.client.Constants
import app.fyreplace.client.data.models.Auth
import app.fyreplace.client.data.services.WildFyreService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository(
    private val wildFyre: WildFyreService,
    private val preferences: SharedPreferences
) {
    var authToken: String
        get() = preferences.getString(Constants.Preferences.AUTH_TOKEN, "").orEmpty()
        private set(value) = preferences.edit { putString(Constants.Preferences.AUTH_TOKEN, value) }

    fun clearAuthToken() {
        authToken = ""
    }

    suspend fun getAuthToken(username: String, password: String) = withContext(Dispatchers.IO) {
        ("Token " + wildFyre.postAuth(Auth(username, password)).token).also { authToken = it }
    }
}
