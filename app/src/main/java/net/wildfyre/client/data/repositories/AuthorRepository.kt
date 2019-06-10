package net.wildfyre.client.data.repositories

import net.wildfyre.client.data.Author
import net.wildfyre.client.data.AuthorPatch
import net.wildfyre.client.data.Services
import net.wildfyre.client.data.await
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody

object AuthorRepository {
    suspend fun getSelf() = Services.webService.getSelf(AuthRepository.authToken).await()

    suspend fun updateSelfBio(bio: String) =
        Services.webService.patchBio(AuthRepository.authToken, AuthorPatch(bio)).await()

    suspend fun updateSelfAvatar(fileName: String, mimeType: String, avatar: ByteArray): Author {
        val avatarBody = RequestBody.create(MediaType.parse(mimeType), avatar)
        val avatarPart = MultipartBody.Part.createFormData("avatar", fileName, avatarBody)
        return Services.webService.putAvatar(AuthRepository.authToken, avatarPart).await()
    }
}
