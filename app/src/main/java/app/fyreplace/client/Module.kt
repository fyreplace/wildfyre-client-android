package app.fyreplace.client

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import app.fyreplace.client.app.R
import org.koin.dsl.module

val applicationModule = module {
    single { get<Context>().resources }

    single { get<Context>().contentResolver }

    single {
        get<Context>().run {
            EncryptedSharedPreferences.create(
                packageName,
                MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
                get(),
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            ).also { newPrefs ->
                val oldName = getString(R.string.app_name)
                val oldPrefs = getSharedPreferences(oldName, Context.MODE_PRIVATE)
                oldPrefs.moveTo(newPrefs)
            }
        }
    }
}
