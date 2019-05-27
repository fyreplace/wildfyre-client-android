package net.wildfyre.client

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.security.ProviderInstaller
import java.lang.ref.WeakReference

class WildFyreApplication : MultiDexApplication() {
    companion object {
        /**
         * Instance of the current application. Initialized in [onCreate].
         */
        private lateinit var instance: WeakReference<Context>

        /**
         * Reference to a context that can be used from anywhere in the application. Using a [WeakReference] allows
         * safely keeping a reference to a context without leaking it. Since this context is created before anything
         * else, and outlives everything else, it's always safe to use it.
         */
        val context: Context
            get() = instance.get()!!

        /**
         * Reference to centralized, application-wide preferences that can be accessed from anywhere in the application.
         */
        val preferences: SharedPreferences
            get() = context.getSharedPreferences(
                context.getString(R.string.app_name),
                Context.MODE_PRIVATE
            )
    }

    override fun onCreate() {
        super.onCreate()
        MultiDex.install(this)
        instance = WeakReference(this)

        // This is needed to solve an issue with okhttp and SSL on Android < 21
        if (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS) {
            ProviderInstaller.installIfNeeded(this)
        }

        /*
        The default night mode needs to be set right when the application is created, before any activity is started.
        If this was done on activity startup, the activity would be recreated as soon as it starts.
         */
        AppCompatDelegate.setDefaultNightMode(
            preferences.getInt(
                Constants.Preferences.UI_THEME,
                Constants.Themes.AUTOMATIC
            )
        )
    }
}
