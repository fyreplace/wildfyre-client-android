package app.fyreplace.client.data.services

import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import okhttp3.OkHttpClient
import org.koin.dsl.module
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.*

val servicesModule = module {
    single<TokenHandler> { SettingsTokenHandler(get()) }

    single {
        OkHttpClient.Builder()
            .addInterceptor(TokenAuthorizationInterceptor(get()))
            .build()
    }

    single {
        Moshi.Builder()
            .add(Date::class.java, Rfc3339DateJsonAdapter())
            .build()
    }

    single<Converter.Factory> { MoshiConverterFactory.create(get()) }

    single {
        Retrofit.Builder()
            .baseUrl(getProperty<String>("data.api.base_url"))
            .addConverterFactory(get())
            .client(get())
            .build()
            .create(WildFyreService::class.java)
    }
}
