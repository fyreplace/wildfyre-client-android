package net.wildfyre.client.data.repositories

import androidx.core.content.edit
import net.wildfyre.client.Constants
import net.wildfyre.client.WildFyreApplication

object SettingsRepository {
    var theme: Int
        get() = WildFyreApplication.preferences.getInt(
            Constants.Preferences.UI_THEME,
            Constants.Themes.AUTOMATIC
        )
        set(value) = WildFyreApplication.preferences.edit { putInt(Constants.Preferences.UI_THEME, value) }
    var showBadge: Boolean
        get() = WildFyreApplication.preferences.getBoolean(Constants.Preferences.UI_BADGE, true)
        set(value) = WildFyreApplication.preferences.edit { putBoolean(Constants.Preferences.UI_BADGE, value) }
}
