package net.wildfyre.client.data.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import net.wildfyre.client.R
import net.wildfyre.client.data.Author
import net.wildfyre.client.data.FailureHandler
import net.wildfyre.client.data.Services
import net.wildfyre.client.data.then
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody

object AuthorRepository {
    private val mutableSelf = MutableLiveData<Author>()

    val self: LiveData<Author> =
        mutableSelf

    fun fetchSelf(fh: FailureHandler) =
        Services.webService.getSelf(AuthRepository.authToken.value!!)
            .then(fh, R.string.failure_request) { mutableSelf.value = it }

    fun updateSelfBio(fh: FailureHandler, bio: String) =
        Services.webService.patchBio(AuthRepository.authToken.value!!, Author().apply { this.bio = bio })
            .then(fh, R.string.failure_profile_edit) { mutableSelf.value = it }

    fun updateSelfAvatar(fh: FailureHandler, fileName: String, mimeType: String, avatar: ByteArray) {
        val avatarBody = RequestBody.create(MediaType.parse(mimeType), avatar)
        val avatarPart = MultipartBody.Part.createFormData("avatar", fileName, avatarBody)
        Services.webService.putAvatar(AuthRepository.authToken.value!!, avatarPart)
            .then(fh, R.string.failure_profile_edit) { mutableSelf.value = it }
    }
}
