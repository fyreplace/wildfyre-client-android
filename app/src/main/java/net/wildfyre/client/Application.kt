package net.wildfyre.client

import android.app.Application
import android.content.Context
import com.google.android.gms.security.ProviderInstaller
import java.lang.ref.WeakReference

class Application : Application() {
    companion object {
        private lateinit var instance: WeakReference<Context>

        val context: Context
            get() = instance.get()!!
    }

    override fun onCreate() {
        super.onCreate()
        instance = WeakReference(this)
        ProviderInstaller.installIfNeeded(this)
    }
}