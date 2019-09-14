package app.fyreplace.client.data.repositories

import app.fyreplace.client.data.models.AuthorPatch
import app.fyreplace.client.data.models.ImageData
import app.fyreplace.client.data.services.WildFyreService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthorRepository(private val wildFyre: WildFyreService, private val auth: AuthRepository) {
    suspend fun getSelf() = withContext(Dispatchers.IO) {
        wildFyre.getSelf(auth.authToken)
    }

    suspend fun getUser(userId: Long) = withContext(Dispatchers.IO) {
        wildFyre.getUser(auth.authToken, userId)
    }

    suspend fun updateSelfBio(bio: String) = withContext(Dispatchers.IO) {
        wildFyre.patchBio(auth.authToken, AuthorPatch(bio))
    }

    suspend fun updateSelfAvatar(image: ImageData) = withContext(Dispatchers.IO) {
        wildFyre.putAvatar(auth.authToken, createFormData("avatar", image))
    }
}
