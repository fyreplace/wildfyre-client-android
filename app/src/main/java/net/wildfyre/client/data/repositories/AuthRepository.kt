package net.wildfyre.client.data.repositories

import androidx.core.content.edit
import net.wildfyre.client.Constants
import net.wildfyre.client.WildFyreApplication
import net.wildfyre.client.data.Services
import net.wildfyre.client.data.await
import net.wildfyre.client.data.models.Auth

object AuthRepository {
    var authToken: String
        get() = WildFyreApplication.preferences.getString(Constants.Preferences.AUTH_TOKEN, "").orEmpty()
        private set(value) = WildFyreApplication.preferences.edit { putString(Constants.Preferences.AUTH_TOKEN, value) }

    fun clearAuthToken() {
        authToken = ""
    }

    suspend fun getAuthToken(username: String, password: String) =
        ("token " + Services.webService.postAuth(Auth(username, password)).await().token)
            .also { authToken = it }
}
