package app.fyreplace.client.data.repositories

import app.fyreplace.client.data.Services
import app.fyreplace.client.data.models.AuthorPatch
import app.fyreplace.client.data.models.ImageData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody

object AuthorRepository {
    suspend fun getSelf() = withContext(Dispatchers.IO) {
        Services.webService.getSelf(AuthRepository.authToken)
    }

    suspend fun getUser(userId: Long) = withContext(Dispatchers.IO) {
        Services.webService.getUser(AuthRepository.authToken, userId)
    }

    suspend fun updateSelfBio(bio: String) = withContext(Dispatchers.IO) {
        Services.webService.patchBio(AuthRepository.authToken, AuthorPatch(bio))
    }

    suspend fun updateSelfAvatar(image: ImageData) = withContext(Dispatchers.IO) {
        Services.webService.putAvatar(
            AuthRepository.authToken,
            MultipartBody.Part.createFormData(
                "avatar",
                image.fileName,
                RequestBody.create(MediaType.parse(image.mimeType), image.bytes)
            )
        )
    }
}
