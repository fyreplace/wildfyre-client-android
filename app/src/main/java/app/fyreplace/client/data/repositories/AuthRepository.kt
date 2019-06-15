package app.fyreplace.client.data.repositories

import androidx.core.content.edit
import app.fyreplace.client.Constants
import app.fyreplace.client.FyreplaceApplication
import app.fyreplace.client.data.Services
import app.fyreplace.client.data.await
import app.fyreplace.client.data.models.Auth

object AuthRepository {
    var authToken: String
        get() = FyreplaceApplication.preferences.getString(Constants.Preferences.AUTH_TOKEN, "").orEmpty()
        private set(value) = FyreplaceApplication.preferences.edit {
            putString(
                Constants.Preferences.AUTH_TOKEN,
                value
            )
        }

    fun clearAuthToken() {
        authToken = ""
    }

    suspend fun getAuthToken(username: String, password: String) =
        ("token " + Services.webService.postAuth(Auth(username, password)).await().token)
            .also { authToken = it }
}
