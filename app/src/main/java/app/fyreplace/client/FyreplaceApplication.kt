package app.fyreplace.client

import androidx.appcompat.app.AppCompatDelegate
import app.fyreplace.client.data.repositories.SettingsRepository
import app.fyreplace.client.data.repositories.repositoriesModule
import app.fyreplace.client.data.services.servicesModule
import app.fyreplace.client.viewmodels.viewModelsModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class FyreplaceApplication : BaseApplication() {
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
    }
}
