package net.wildfyre.client

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.gms.security.ProviderInstaller
import java.lang.ref.WeakReference

class Application : Application() {
    companion object {
        private lateinit var instance: WeakReference<Context>

        val context: Context
            get() = instance.get()!!

        val preferences: SharedPreferences
            get() = context.getSharedPreferences(
                context.getString(R.string.app_name),
                Context.MODE_PRIVATE
            )
    }

    override fun onCreate() {
        super.onCreate()
        instance = WeakReference(this)
        ProviderInstaller.installIfNeeded(this)

        AppCompatDelegate.setDefaultNightMode(
            getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE).getInt(
                Constants.Preferences.UI_THEME,
                AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            )
        )
    }
}