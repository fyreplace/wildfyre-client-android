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

        const val IMAGE_MAX_FILE_SIZE = 512 * 1024
        const val IMAGE_MAX_AREA = 1920 * 1080
    }

    /**
     * Simplified constants for day/night mode, merging [AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM]
     * and [AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY].
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
            private const val DOMAIN = "wildfyre.net"
            const val WEBSITE = "https://$DOMAIN"
            const val OPEN_SOURCE = "https://phabricator.$DOMAIN"
            const val FAQ = "$WEBSITE#home-faq"
            const val TERMS = "$WEBSITE/pages/terms-of-service"
            const val PRIVACY = "$WEBSITE/pages/privacy-policy"
            const val REGISTER = "https://client.$DOMAIN/register"
        }
    }
}
