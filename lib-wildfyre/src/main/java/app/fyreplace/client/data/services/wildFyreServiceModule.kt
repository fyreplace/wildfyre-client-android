package app.fyreplace.client.data.services

import com.google.gson.GsonBuilder
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val wildFyreServiceModule = module {
    single {
        Retrofit.Builder()
            .baseUrl(getProperty<String>("data.api.base_url"))
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().serializeNulls().create()))
            .client(get())
            .build()
            .create(WildFyreService::class.java)
    }
}
