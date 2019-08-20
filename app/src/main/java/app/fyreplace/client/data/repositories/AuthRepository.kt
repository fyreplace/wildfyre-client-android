package app.fyreplace.client.data.repositories

import androidx.core.content.edit
import app.fyreplace.client.Constants
import app.fyreplace.client.FyreplaceApplication
import app.fyreplace.client.data.Services
import app.fyreplace.client.data.models.Auth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object AuthRepository {
    var authToken: String
        get() = FyreplaceApplication.preferences
            .getString(Constants.Preferences.AUTH_TOKEN, "").orEmpty()
        private set(value) = FyreplaceApplication.preferences
            .edit { putString(Constants.Preferences.AUTH_TOKEN, value) }

    fun clearAuthToken() {
        authToken = ""
    }

    suspend fun getAuthToken(username: String, password: String) = withContext(Dispatchers.IO) {
        ("token " + Services.webService.postAuth(Auth(username, password)).token)
            .also { authToken = it }
    }
}
