package net.wildfyre.client

import android.os.Build
import androidx.appcompat.app.AppCompatDelegate

object Constants {
    object Api {
        const val BASE_URL = "https://api.wildfyre.net"
        val IMAGE_REGEX = Regex("(\\[img:\\s*(\\d+)\\])")
    }

    object Save {
        const val ACTIVITY_NAVIGATION = "activity.navigation"
    }

    object Themes {
        val AUTOMATIC = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM else
            AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
        const val LIGHT = AppCompatDelegate.MODE_NIGHT_NO
        const val DARK = AppCompatDelegate.MODE_NIGHT_YES
    }

    object Preferences {
        const val UI_THEME = "ui.theme"
        const val UI_BADGE = "ui.badge"
        const val AUTH_TOKEN = "auth.token"
        const val AREA_PREFERRED = "area.preferred"
    }

    object Links {
        private const val MAIN_ADDRESS = "https://wildfyre.net/"
        const val ABOUT_US = MAIN_ADDRESS + "about-us"
        const val OPEN_SOURCE = MAIN_ADDRESS + "open-source"
        const val FAQ = MAIN_ADDRESS + "frequently-asked-questions"
        const val TERMS_AND_CONDITIONS = MAIN_ADDRESS + "terms-and-conditions"
        const val PRIVACY_POLICY = MAIN_ADDRESS + "privacy-policy"
        const val FACEBOOK = "https://www.facebook.com/wildfyreapp/"
        const val TWITTER = "https://twitter.com/wildfyreapp"
        const val TELEGRAM = "https://t.me/WildFyreApp"
    }
}