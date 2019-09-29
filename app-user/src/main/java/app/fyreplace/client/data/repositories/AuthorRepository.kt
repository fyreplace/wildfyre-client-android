package app.fyreplace.client.data.repositories

import app.fyreplace.client.data.models.AuthorPatch
import app.fyreplace.client.data.models.ImageData
import app.fyreplace.client.data.services.WildFyreService
import app.fyreplace.client.data.services.createFormData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthorRepository(private val wildFyre: WildFyreService) {
    suspend fun getSelf() = withContext(Dispatchers.IO) { wildFyre.getSelf() }

    suspend fun getUser(userId: Long) = withContext(Dispatchers.IO) { wildFyre.getUser(userId) }

    suspend fun updateSelfBio(bio: String) = withContext(Dispatchers.IO) {
        wildFyre.patchBio(AuthorPatch(bio))
    }

    suspend fun updateSelfAvatar(image: ImageData) = withContext(Dispatchers.IO) {
        wildFyre.putAvatar(createFormData("avatar", image))
    }
}
