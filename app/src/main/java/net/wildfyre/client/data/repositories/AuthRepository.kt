package net.wildfyre.client.data.repositories

import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import net.wildfyre.client.Constants
import net.wildfyre.client.WildFyreApplication
import net.wildfyre.client.data.Auth
import net.wildfyre.client.data.Services
import net.wildfyre.client.data.await

object AuthRepository {
    private val mutableAuthToken = MutableLiveData<String>()

    val authToken: LiveData<String> = mutableAuthToken

    init {
        mutableAuthToken.value = WildFyreApplication.preferences.getString(Constants.Preferences.AUTH_TOKEN, "")
        WildFyreApplication.preferences.registerOnSharedPreferenceChangeListener { sharedPreferences, key ->
            if (key == Constants.Preferences.AUTH_TOKEN) {
                mutableAuthToken.postValue(sharedPreferences.getString(key, ""))
            }
        }
    }

    fun clearAuthToken() = setAuthToken("")

    suspend fun fetchAuthToken(username: String, password: String) =
        setAuthToken("token " + Services.webService.postAuth(Auth(username, password)).await().token)

    private fun setAuthToken(token: String) =
        WildFyreApplication.preferences.edit { putString(Constants.Preferences.AUTH_TOKEN, token) }
}
