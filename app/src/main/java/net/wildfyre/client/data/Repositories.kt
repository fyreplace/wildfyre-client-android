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

object SettingsRepository {
    private val mutableTheme = MutableLiveData<Int>()
    private val mutableBadgeToggle = MutableLiveData<Boolean>()

    val theme: LiveData<Int> = mutableTheme
    val badgeToggle: LiveData<Boolean> = mutableBadgeToggle

    init {
        mutableTheme.value = Application.preferences.getInt(Constants.Preferences.UI_THEME, Constants.Themes.AUTOMATIC)
        mutableBadgeToggle.value = Application.preferences.getBoolean(Constants.Preferences.UI_BADGE, true)
    }

    fun setTheme(theme: Int) {
        Application.preferences.edit { putInt(Constants.Preferences.UI_THEME, theme) }
        mutableTheme.value = theme
    }

    fun toggleBadge(show: Boolean) {
        Application.preferences.edit { putBoolean(Constants.Preferences.UI_BADGE, show) }
        mutableBadgeToggle.value = show
    }
}

object AuthRepository {
    private val mutableAuthToken = MutableLiveData<String>()

    val authToken: LiveData<String> = mutableAuthToken

    init {
        mutableAuthToken.value = Application.preferences.getString(Constants.Preferences.AUTH_TOKEN, "")
    }

    fun clearAuthToken() = setAuthToken("")

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

object AuthorRepository {
    private val mutableSelf = MutableLiveData<Author>()

    val self: LiveData<Author> = mutableSelf

    fun fetchSelf(fh: FailureHandler) =
        Services.webService.getSelf(AuthRepository.authToken.value!!).then(fh, R.string.failure_request) {
            mutableSelf.value = it
        }

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

object AreaRepository {
    private val mutableAreas = MutableLiveData<List<Area>>()
    private val mutablePreferredAreaName = MutableLiveData<String>()
    private val mutablePreferredAreaReputation = MutableLiveData<Reputation>()

    val areas: LiveData<List<Area>> = mutableAreas
    val preferredAreaName: LiveData<String> = mutablePreferredAreaName
    val preferredAreaReputation: LiveData<Reputation> = mutablePreferredAreaReputation

    init {
        Application.preferences.getString(Constants.Preferences.AREA_PREFERRED, null)?.let {
            mutablePreferredAreaName.value = it
        }
    }

    fun fetchAreas(fh: FailureHandler) =
        Services.webService.getAreas(AuthRepository.authToken.value!!).then(fh, R.string.failure_request) {
            mutableAreas.value = it
        }

    fun fetchAreaReputation(fh: FailureHandler, areaName: String) =
        Services.webService.getAreaRep(AuthRepository.authToken.value!!, areaName).then(fh, R.string.failure_request) {
            mutablePreferredAreaReputation.value = it
        }

    fun setPreferredAreaName(name: String) {
        Application.preferences.edit { putString(Constants.Preferences.AREA_PREFERRED, name) }
        mutablePreferredAreaName.value = name
    }
}

object NotificationRepository {
    private const val BUCKET_SIZE = 12L

    private val mutableSuperNotification = MutableLiveData<SuperNotification>()
    private val mutableNotifications = MutableLiveData<List<Notification>>()
    private var notificationOffset = 0L
    private var fetchingContent = false

    val superNotification: LiveData<SuperNotification> = mutableSuperNotification
    val notifications: LiveData<List<Notification>> = mutableNotifications

    init {
        resetNotifications()
    }

    fun fetchNextNotifications(fh: FailureHandler, forContent: Boolean) {
        if (fetchingContent) {
            return
        }

        if (forContent) {
            fetchingContent = true
        }

        Services.webService.getNotifications(
            AuthRepository.authToken.value!!,
            if (forContent) BUCKET_SIZE else 1,
            notificationOffset
        ).then(fh, R.string.failure_request) {
            fetchingContent = false
            mutableSuperNotification.value = it

            if (forContent) {
                it.results?.run {
                    notificationOffset += size
                    mutableNotifications.value = mutableNotifications.value!! + this
                }
            }
        }
    }

    fun resetNotifications() {
        notificationOffset = 0
        mutableSuperNotification.value = mutableSuperNotification.value?.apply {
            count = 0
            results = listOf()
        } ?: SuperNotification()
        mutableNotifications.value = listOf()
    }

    fun clearNotifications(fh: FailureHandler) {
        Services.webService.deleteNotifications(AuthRepository.authToken.value!!)
            .then(fh, R.string.failure_request) { resetNotifications() }
    }
}