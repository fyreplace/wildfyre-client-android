package app.fyreplace.client.data.services

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val servicesModule = module {
    single<TokenHandler> { SettingsTokenHandler(get()) }

    single {
        OkHttpClient.Builder()
            .addInterceptor(TokenAuthorizationInterceptor(get()))
            .build()
    }

    single {
        Retrofit.Builder()
            .baseUrl(getProperty<String>("data.api.base_url"))
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().serializeNulls().create()))
            .client(get())
            .build()
            .create(WildFyreService::class.java)
    }
}
