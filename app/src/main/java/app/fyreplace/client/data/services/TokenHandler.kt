package app.fyreplace.client.data.services

import android.content.SharedPreferences
import androidx.core.content.edit

class TokenHandler(private val preferences: SharedPreferences) {
    var authToken: String
        get() = preferences.getString(PREFS_KEY_AUTH_TOKEN, "").orEmpty()
        set(value) = preferences.edit { putString(PREFS_KEY_AUTH_TOKEN, value) }

    private companion object {
        const val PREFS_KEY_AUTH_TOKEN = "auth.token"
    }
}
