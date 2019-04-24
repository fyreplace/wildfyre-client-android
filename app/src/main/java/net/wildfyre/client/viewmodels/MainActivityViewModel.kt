package net.wildfyre.client.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import net.wildfyre.client.Constants
import net.wildfyre.client.R
import net.wildfyre.client.data.AuthRepository
import net.wildfyre.client.data.AuthorRepository
import net.wildfyre.client.data.NotificationRepository
import net.wildfyre.client.data.SettingsRepository

class MainActivityViewModel(application: Application) : FailureHandlingViewModel(application) {
    private var _userAvatarFileName: String? = null
    private var _userAvatarMimeType: String? = null
    private val _userAvatarNewData = MutableLiveData<ByteArray>()
    private val _notificationBadgeVisible = MutableLiveData<Boolean>()
    private val _notificationCount: LiveData<Long> =
        Transformations.map(NotificationRepository.superNotification) { it.count ?: 0 }

    val authToken: LiveData<String> = AuthRepository.authToken
    val userName: LiveData<String> = Transformations.map(AuthorRepository.self) { it.name }
    val userBio: LiveData<String> = Transformations.map(AuthorRepository.self) { it.bio }
    val userAvatar: LiveData<String> = Transformations.map(AuthorRepository.self) { it.avatar }
    val userAvatarNewData: LiveData<ByteArray> = _userAvatarNewData
    val notificationCount: LiveData<Long> = _notificationCount
    val notificationCountText: LiveData<String> =
        Transformations.map(_notificationCount) { if (it < 1000) it.toString() else "999" }
    val notificationBadgeVisible: LiveData<Boolean> = _notificationBadgeVisible
    val selectedThemeIndex = MutableLiveData<Int>()

    init {
        if (AuthRepository.authToken.value!!.isNotEmpty()) {
            updateInterfaceInformation()
        }

        selectedThemeIndex.value = THEMES.indexOfFirst { it == SettingsRepository.theme.value }
    }

    val clearAuthToken = AuthRepository::clearAuthToken

    fun updateProfile() = AuthorRepository.fetchSelf(this)

    fun updateNotifications() = NotificationRepository.fetchSuperNotification(this, 0, 0)

    fun updateInterfaceInformation() {
        updateProfile()
        updateNotifications()
    }

    fun setProfile(bio: String) {
        if (bio != userBio.value) {
            AuthorRepository.updateSelfBio(this, bio)
        }

        userAvatarNewData.value?.let {
            AuthorRepository.updateSelfAvatar(
                this,
                _userAvatarFileName!!,
                _userAvatarMimeType!!,
                it
            )
        }
    }

    fun setPendingProfileAvatar(fileName: String, mimeType: String, avatar: ByteArray) {
        _userAvatarFileName = fileName
        _userAvatarMimeType = mimeType
        _userAvatarNewData.value = avatar
    }

    fun resetPendingProfileAvatar() {
        _userAvatarFileName = null
        _userAvatarMimeType = null
        _userAvatarNewData.value = null
    }

    fun setNotificationBadgeVisible(visible: Boolean) {
        _notificationBadgeVisible.value = visible
    }

    val setTheme = SettingsRepository::setTheme

    companion object {
        val THEMES = arrayOf(
            Constants.Themes.AUTOMATIC,
            Constants.Themes.LIGHT,
            Constants.Themes.DARK
        )

        val NAVIGATION_LINKS = mapOf(
            Pair(R.id.about_us, Constants.Links.ABOUT_US),
            Pair(R.id.open_source, Constants.Links.OPEN_SOURCE),
            Pair(R.id.faq, Constants.Links.FAQ),
            Pair(R.id.terms_and_conditions, Constants.Links.TERMS_AND_CONDITIONS),
            Pair(R.id.privacy_policy, Constants.Links.PRIVACY_POLICY)
        )
    }
}