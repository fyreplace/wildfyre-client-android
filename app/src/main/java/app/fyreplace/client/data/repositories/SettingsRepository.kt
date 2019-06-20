package app.fyreplace.client.data.repositories

import androidx.core.content.edit
import app.fyreplace.client.Constants
import app.fyreplace.client.FyreplaceApplication

object SettingsRepository {
    var theme: Int
        get() = FyreplaceApplication.preferences.getInt(Constants.Preferences.UI_THEME, Constants.Themes.AUTOMATIC)
        set(value) = FyreplaceApplication.preferences.edit { putInt(Constants.Preferences.UI_THEME, value) }
    var showBadge: Boolean
        get() = FyreplaceApplication.preferences.getBoolean(Constants.Preferences.UI_BADGE, true)
        set(value) = FyreplaceApplication.preferences.edit { putBoolean(Constants.Preferences.UI_BADGE, value) }
}
