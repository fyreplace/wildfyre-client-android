package app.fyreplace.client.data.services

import app.fyreplace.client.Constants
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val servicesModule = module {
    single { TokenHandler(get()) }

    single {
        OkHttpClient.Builder()
            .addInterceptor(TokenAuthorizationInterceptor(get()))
            .build()
    }

    single {
        Retrofit.Builder()
            .baseUrl(Constants.Api.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().serializeNulls().create()))
            .client(get())
            .build()
            .create(WildFyreService::class.java)
    }
}
