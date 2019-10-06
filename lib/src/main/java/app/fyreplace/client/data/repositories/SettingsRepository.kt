package app.fyreplace.client.data.repositories

import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit

class SettingsRepository(private val preferences: SharedPreferences) {
    var theme: Int
        get() = preferences.getInt(
            PREFS_KEY_UI_THEME,
            Themes.AUTOMATIC
        )
        set(value) = preferences.edit { putInt(PREFS_KEY_UI_THEME, value) }
    var showBadge: Boolean
        get() = preferences.getBoolean(PREFS_KEY_UI_BADGE, true)
        set(value) = preferences.edit { putBoolean(PREFS_KEY_UI_BADGE, value) }

    private companion object {
        const val PREFS_KEY_UI_THEME = "ui.theme"
        const val PREFS_KEY_UI_BADGE = "ui.badge"
    }

    object Themes {
        val AUTOMATIC =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
                AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            else
                AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
        const val LIGHT = AppCompatDelegate.MODE_NIGHT_NO
        const val DARK = AppCompatDelegate.MODE_NIGHT_YES
    }
}
