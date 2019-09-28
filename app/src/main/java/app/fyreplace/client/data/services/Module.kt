package app.fyreplace.client.data.services

import okhttp3.OkHttpClient
import org.koin.dsl.module

val servicesModule = module {
    single { TokenHandler(get()) }

    single {
        OkHttpClient.Builder()
            .addInterceptor(TokenAuthorizationInterceptor(get()))
            .build()
    }
}
