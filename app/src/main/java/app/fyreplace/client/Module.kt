package app.fyreplace.client

import android.content.Context
import android.os.Build
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import app.fyreplace.client.app.R
import org.koin.dsl.module

val applicationModule = module {
    single { get<Context>().resources }

    single { get<Context>().contentResolver }

    single {
        get<Context>().run {
            val plainTextPrefs = getSharedPreferences(
                getString(R.string.app_name),
                Context.MODE_PRIVATE
            )

            return@run if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                EncryptedSharedPreferences.create(
                    packageName,
                    MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
                    get(),
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                ).also { plainTextPrefs.moveTo(it) }
            } else {
                plainTextPrefs
            }
        }
    }
}
