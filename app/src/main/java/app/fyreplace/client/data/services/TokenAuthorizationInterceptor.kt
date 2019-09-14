package app.fyreplace.client.data.services

import okhttp3.Interceptor
import okhttp3.Response

class TokenAuthorizationInterceptor(private val tokenHandler: TokenHandler) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()

        tokenHandler.authToken.takeIf { it.isNotBlank() }
            ?.let { builder.addHeader("Authorization", it) }

        return chain.proceed(builder.build())
    }
}
