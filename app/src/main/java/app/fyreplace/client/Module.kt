package app.fyreplace.client

import android.content.Context
import app.fyreplace.client.app.R
import org.koin.dsl.module

val applicationModule = module {
    single {
        get<Context>().run {
            getSharedPreferences(
                getString(R.string.app_name),
                Context.MODE_PRIVATE
            )
        }
    }
}
