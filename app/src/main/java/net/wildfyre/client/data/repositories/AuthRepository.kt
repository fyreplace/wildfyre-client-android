package net.wildfyre.client.data.repositories

import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import net.wildfyre.client.Constants
import net.wildfyre.client.R
import net.wildfyre.client.WildFyreApplication
import net.wildfyre.client.data.Auth
import net.wildfyre.client.data.FailureHandler
import net.wildfyre.client.data.Services
import net.wildfyre.client.data.then

object AuthRepository {
    private val mutableAuthToken = MutableLiveData<String>()

    val authToken: LiveData<String> = mutableAuthToken

    init {
        mutableAuthToken.value = WildFyreApplication.preferences.getString(Constants.Preferences.AUTH_TOKEN, "")
    }

    fun clearAuthToken() = setAuthToken("")

    fun fetchAuthToken(fh: FailureHandler, username: String, password: String) {
        Services.webService.postAuth(Auth(username, password))
            .then(fh, R.string.failure_login) { setAuthToken("token " + it.token) }
    }

    private fun setAuthToken(token: String) {
        mutableAuthToken.value = token
        WildFyreApplication.preferences.edit { putString(Constants.Preferences.AUTH_TOKEN, token) }
    }
}
