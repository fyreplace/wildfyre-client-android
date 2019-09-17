package app.fyreplace.client

import androidx.multidex.MultiDexApplication
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.security.ProviderInstaller

abstract class BaseApplication : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()

        // This is needed to solve an issue with okhttp and SSL on Android < 21
        if (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS) {
            ProviderInstaller.installIfNeeded(this)
        }
    }
}
