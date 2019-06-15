package app.fyreplace.client.data.repositories

import app.fyreplace.client.data.Services
import app.fyreplace.client.data.await
import app.fyreplace.client.data.models.Author
import app.fyreplace.client.data.models.AuthorPatch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody

object AuthorRepository {
    suspend fun getSelf() = Services.webService.getSelf(AuthRepository.authToken).await()

    suspend fun getUser(userId: Long) = Services.webService.getUser(AuthRepository.authToken, userId).await()

    suspend fun updateSelfBio(bio: String) =
        Services.webService.patchBio(AuthRepository.authToken, AuthorPatch(bio)).await()

    suspend fun updateSelfAvatar(fileName: String, mimeType: String, avatar: ByteArray): Author {
        val avatarBody = RequestBody.create(MediaType.parse(mimeType), avatar)
        val avatarPart = MultipartBody.Part.createFormData("avatar", fileName, avatarBody)
        return Services.webService.putAvatar(AuthRepository.authToken, avatarPart).await()
    }
}
