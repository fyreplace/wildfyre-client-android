package app.fyreplace.client.data.services

import android.content.SharedPreferences
import androidx.core.content.edit
import app.fyreplace.client.Constants

class TokenHandler(private val preferences: SharedPreferences) {
    var authToken: String
        get() = preferences.getString(Constants.Preferences.AUTH_TOKEN, "").orEmpty()
        set(value) = preferences.edit { putString(Constants.Preferences.AUTH_TOKEN, value) }
}
