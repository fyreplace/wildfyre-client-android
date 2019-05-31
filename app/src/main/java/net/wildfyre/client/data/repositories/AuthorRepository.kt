package net.wildfyre.client.data.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import net.wildfyre.client.data.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody

object AuthorRepository {
    private val mutableSelf = MutableLiveData<Author>()

    val self: LiveData<Author> =
        mutableSelf

    fun fetchSelf(fh: FailureHandler) =
        Services.webService.getSelf(AuthRepository.authToken.value!!)
            .then(fh) { mutableSelf.value = it }

    fun updateSelfBio(fh: FailureHandler, bio: String) =
        Services.webService.patchBio(AuthRepository.authToken.value!!, AuthorPatch(bio))
            .then(fh) { mutableSelf.value = it }

    fun updateSelfAvatar(fh: FailureHandler, fileName: String, mimeType: String, avatar: ByteArray) {
        val avatarBody = RequestBody.create(MediaType.parse(mimeType), avatar)
        val avatarPart = MultipartBody.Part.createFormData("avatar", fileName, avatarBody)
        Services.webService.putAvatar(AuthRepository.authToken.value!!, avatarPart)
            .then(fh) { mutableSelf.value = it }
    }
}
