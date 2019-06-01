package net.wildfyre.client.data.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import net.wildfyre.client.data.Author
import net.wildfyre.client.data.AuthorPatch
import net.wildfyre.client.data.Services
import net.wildfyre.client.data.await
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody

object AuthorRepository {
    private val mutableSelf = MutableLiveData<Author>()

    val self: LiveData<Author> = mutableSelf

    suspend fun fetchSelf() =
        mutableSelf.postValue(Services.webService.getSelf(AuthRepository.authToken.value!!).await())

    suspend fun updateSelfBio(bio: String) =
        mutableSelf.postValue(Services.webService.patchBio(AuthRepository.authToken.value!!, AuthorPatch(bio)).await())

    suspend fun updateSelfAvatar(fileName: String, mimeType: String, avatar: ByteArray) {
        val avatarBody = RequestBody.create(MediaType.parse(mimeType), avatar)
        val avatarPart = MultipartBody.Part.createFormData("avatar", fileName, avatarBody)
        mutableSelf.postValue(Services.webService.putAvatar(AuthRepository.authToken.value!!, avatarPart).await())
    }
}
