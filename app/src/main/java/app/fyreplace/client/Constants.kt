package app.fyreplace.client

import android.os.Build
import androidx.appcompat.app.AppCompatDelegate

object Constants {
    /**
     * Constants related to using the WildFyre API.
     */
    object Api {
        const val BASE_URL = "https://api.wildfyre.net"
        val IMAGE_REGEX = Regex("\n*\\[img:\\s*(\\d+)]\n*", RegexOption.MULTILINE)
        val YOUTUBE_REGEX =
            Regex("(?:https?://)?(?:www\\.)?youtu(?:be\\.(?:\\w+)/watch\\?v=|\\.be/)(\\w+)")

        fun postShareUrl(areaName: String, postId: Long) =
            "https://client.wildfyre.net/areas/$areaName/$postId"

        fun postShareUrl(areaName: String, postId: Long, selectedCommentId: Long) =
            "https://client.wildfyre.net/areas/$areaName/$postId/$selectedCommentId"

        fun userShareUrl(userId: Long) =
            "https://client.wildfyre.net/user/$userId"

        fun youtubeThumbnail(videoId: String) =
            "https://img.youtube.com/vi/$videoId/0.jpg"
    }

    /**
     * Simplified constants for day/night mode, merging [AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM] and
     * [AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY].
     */
    object Themes {
        val AUTOMATIC =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
                AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            else
                AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
        const val LIGHT = AppCompatDelegate.MODE_NIGHT_NO
        const val DARK = AppCompatDelegate.MODE_NIGHT_YES
    }

    /**
     * Keys used for storing user preferences.
     */
    object Preferences {
        const val UI_THEME = "ui.theme"
        const val UI_BADGE = "ui.badge"
        const val AUTH_TOKEN = "auth.token"
        const val AREA_PREFERRED = "area.preferred"
    }

    /**
     * Links used in the navigation panel.
     */
    object Links {
        object Fyreplace {
            private const val MAIN_ADDRESS = "https://github.com/fyreplace"
            const val WEBSITE = MAIN_ADDRESS
            const val OPEN_SOURCE = "$MAIN_ADDRESS/fyreplace-client-android"
        }

        object WildFyre {
            private const val MAIN_ADDRESS = "https://wildfyre.net"
            const val WEBSITE = MAIN_ADDRESS
            const val OPEN_SOURCE = "$MAIN_ADDRESS/open-source"
            const val FAQ = "$MAIN_ADDRESS/frequently-asked-questions"
            const val TERMS_AND_CONDITIONS = "$MAIN_ADDRESS/terms-and-conditions"
            const val PRIVACY_POLICY = "$MAIN_ADDRESS/privacy-policy"
            const val REGISTER = "https://client.wildfyre.net/register"
        }
    }
}
