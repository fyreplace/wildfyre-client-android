package app.fyreplace.client.data.repositories

import app.fyreplace.client.data.models.Auth
import app.fyreplace.client.data.services.TokenHandler
import app.fyreplace.client.data.services.WildFyreService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository(
    private val wildFyre: WildFyreService,
    private val tokenHandler: TokenHandler
) {
    val authToken: String
        get() = tokenHandler.authToken

    fun clearAuthToken() {
        tokenHandler.authToken = ""
    }

    suspend fun getAuthToken(username: String, password: String) = withContext(Dispatchers.IO) {
        ("Token " + wildFyre.postAuth(Auth(username, password)).token)
            .also { tokenHandler.authToken = it }
    }
}
