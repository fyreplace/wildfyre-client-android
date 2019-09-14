package app.fyreplace.client.data.repositories

import android.content.SharedPreferences
import androidx.core.content.edit
import app.fyreplace.client.Constants

class SettingsRepository(private val preferences: SharedPreferences) {
    var theme: Int
        get() = preferences.getInt(Constants.Preferences.UI_THEME, Constants.Themes.AUTOMATIC)
        set(value) = preferences.edit { putInt(Constants.Preferences.UI_THEME, value) }
    var showBadge: Boolean
        get() = preferences.getBoolean(Constants.Preferences.UI_BADGE, true)
        set(value) = preferences.edit { putBoolean(Constants.Preferences.UI_BADGE, value) }
}
