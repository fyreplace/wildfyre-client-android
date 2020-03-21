package app.fyreplace.client

import android.content.SharedPreferences
import androidx.core.content.edit

fun SharedPreferences.moveTo(other: SharedPreferences) = other.edit {
    for (entry in all) {
        val key = entry.key

        @Suppress("UNCHECKED_CAST")
        when (val value = entry.value) {
            is Boolean -> putBoolean(key, value)
            is Int -> putInt(key, value)
            is Long -> putLong(key, value)
            is Float -> putFloat(key, value)
            is String -> putString(key, value)
            else -> (value as? Set<String>)?.let { putStringSet(key, it) }
        }

        edit { remove(key) }
    }
}
