package net.wildfyre.client.data

import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import net.wildfyre.client.Application
import net.wildfyre.client.Constants
import net.wildfyre.client.R
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody

object AuthRepository {
    private val mutableAuthToken = MutableLiveData<String>()

    val authToken: LiveData<String>
        get() = mutableAuthToken

    init {
        mutableAuthToken.value = Application.preferences.getString(Constants.Preferences.AUTH_TOKEN, "") ?: ""
    }

    fun clearAuthToken() {
        setAuthToken("")
    }

    fun fetchAuthToken(fh: FailureHandler, username: String, password: String) {
        val auth = Account.Auth().also { it.username = username; it.password = password }

        Services.webService.postAuth(auth).then(fh, R.string.failure_login) {
            if (it.token != null) {
                setAuthToken("token " + it.token)
            }
        }
    }

    private fun setAuthToken(token: String) {
        mutableAuthToken.value = token
        Application.preferences.edit { putString(Constants.Preferences.AUTH_TOKEN, token) }
    }
}

object AccountRepository {
    private val mutableAccount = MutableLiveData<Account>()

    val account: LiveData<Account>
        get() = mutableAccount

    init {
        mutableAccount.value = Account().apply { username = Application.context.getString(android.R.string.untitled) }
    }

    fun fetchAccount(fh: FailureHandler) {
        Services.webService.getAccount(AuthRepository.authToken.value!!).then(fh, R.string.failure_request) {
            mutableAccount.value = it
        }
    }
}

object AuthorRepository {
    private val mutableSelf = MutableLiveData<Author>()

    val self: LiveData<Author>
        get() = mutableSelf

    fun fetchSelf(fh: FailureHandler) {
        Services.webService.getSelf(AuthRepository.authToken.value!!).then(fh, R.string.failure_request) {
            mutableSelf.value = it
        }
    }

    fun updateSelfBio(fh: FailureHandler, bio: String) {
        Services.webService.patchBio(AuthRepository.authToken.value!!, Author().apply { this.bio = bio })
            .then(fh, R.string.failure_profile_edit) { mutableSelf.value = it }
    }

    fun updateSelfAvatar(fh: FailureHandler, fileName: String, mimeType: String, avatar: ByteArray) {
        val avatarBody = RequestBody.create(MediaType.parse(mimeType), avatar)
        val avatarPart = MultipartBody.Part.createFormData("avatar", fileName, avatarBody)
        Services.webService.putAvatar(AuthRepository.authToken.value!!, avatarPart)
            .then(fh, R.string.failure_profile_edit) { mutableSelf.value = it }
    }
}

object AreaRepository {
    private val mutableAreas = MutableLiveData<List<Area>>()
    private val mutablePreferredAreaName = MutableLiveData<String>()
    private val mutablePreferredAreaReputation = MutableLiveData<Reputation>()

    val areas: LiveData<List<Area>>
        get() = mutableAreas
    val preferredAreaName: LiveData<String>
        get() = mutablePreferredAreaName
    val preferredAreaReputation: LiveData<Reputation>
        get() = mutablePreferredAreaReputation

    init {
        Application.preferences.getString(Constants.Preferences.PREFERRED_AREA, null)?.let {
            mutablePreferredAreaName.value = it
        }
    }

    fun fetchAreas(fh: FailureHandler) {
        Services.webService.getAreas(AuthRepository.authToken.value!!).then(fh, R.string.failure_request) {
            mutableAreas.value = it
        }
    }

    fun fetchAreaReputation(fh: FailureHandler, areaName: String) {
        Services.webService.getAreaRep(AuthRepository.authToken.value!!, areaName).then(fh, R.string.failure_request) {
            mutablePreferredAreaReputation.value = it
        }
    }

    fun setPreferredAreaName(name: String) {
        Application.preferences.edit { putString(Constants.Preferences.PREFERRED_AREA, name) }
        mutablePreferredAreaName.value = name
    }
}