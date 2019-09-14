package app.fyreplace.client

import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDexApplication
import app.fyreplace.client.data.repositories.SettingsRepository
import app.fyreplace.client.data.repositories.repositoriesModule
import app.fyreplace.client.data.services.servicesModule
import app.fyreplace.client.viewmodels.viewModelsModule
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.security.ProviderInstaller
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class FyreplaceApplication : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()

        val koinApp = startKoin {
            androidContext(this@FyreplaceApplication)
            modules(applicationModule + servicesModule + repositoriesModule + viewModelsModule)
        }

        /*
        The default night mode needs to be set right when the application is created, before any activity is started.
        If this was done on activity startup, the activity would be recreated as soon as it starts.
         */
        AppCompatDelegate.setDefaultNightMode(koinApp.koin.get<SettingsRepository>().theme)

        // This is needed to solve an issue with okhttp and SSL on Android < 21
        if (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS) {
            ProviderInstaller.installIfNeeded(this)
        }
    }
}
