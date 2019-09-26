package app.fyreplace.client.data.repositories

import android.content.SharedPreferences
import androidx.core.content.edit
import app.fyreplace.client.Constants

class SettingsRepository(private val preferences: SharedPreferences) {
    var theme: Int
        get() = preferences.getInt(PREFS_KEY_UI_THEME, Constants.Themes.AUTOMATIC)
        set(value) = preferences.edit { putInt(PREFS_KEY_UI_THEME, value) }
    var showBadge: Boolean
        get() = preferences.getBoolean(PREFS_KEY_UI_BADGE, true)
        set(value) = preferences.edit { putBoolean(PREFS_KEY_UI_BADGE, value) }

    private companion object {
        const val PREFS_KEY_UI_THEME = "ui.theme"
        const val PREFS_KEY_UI_BADGE = "ui.badge"
    }
}
