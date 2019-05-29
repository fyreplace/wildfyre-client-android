package net.wildfyre.client.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import net.wildfyre.client.Constants
import net.wildfyre.client.R
import net.wildfyre.client.data.repositories.AuthRepository
import net.wildfyre.client.data.repositories.AuthorRepository
import net.wildfyre.client.data.repositories.NotificationRepository
import net.wildfyre.client.data.repositories.SettingsRepository

class MainActivityViewModel(application: Application) : FailureHandlingViewModel(application) {
    private var _userAvatarFileName: String? = null
    private var _userAvatarMimeType: String? = null
    private val _userAvatarNewData = MutableLiveData<ByteArray>()
    private val _notificationBadgeVisible = MutableLiveData<Boolean>()
    private val _notificationCount: LiveData<Int> =
        Transformations.map(NotificationRepository.superNotification) { it.count ?: 0 }

    var startupLogin = true
        private set
    val authToken: LiveData<String> = AuthRepository.authToken
    val userName: LiveData<String> = Transformations.map(AuthorRepository.self) { it.name }
    val userBio: LiveData<String> = Transformations.map(AuthorRepository.self) { it.bio }
    val userAvatar: LiveData<String> = Transformations.map(AuthorRepository.self) { it.avatar }
    val userAvatarNewData: LiveData<ByteArray> = _userAvatarNewData
    val notificationCount: LiveData<Int> = _notificationCount
    val notificationCountText: LiveData<String> =
        Transformations.map(_notificationCount) { if (it < 100) it.toString() else "99" }
    val notificationBadgeVisible: LiveData<Boolean> = _notificationBadgeVisible

    val selectedThemeIndex = MutableLiveData<Int>()
    val shouldShowNotificationBadge = MutableLiveData<Boolean>()

    init {
        selectedThemeIndex.value = THEMES.indexOfFirst { it == SettingsRepository.theme }
        selectedThemeIndex.observeForever { SettingsRepository.theme = THEMES[it] }
        shouldShowNotificationBadge.value = SettingsRepository.showBadge
        shouldShowNotificationBadge.observeForever { SettingsRepository.showBadge = it }
    }

    fun logout() {
        startupLogin = false
        AuthRepository.clearAuthToken()
    }

    fun updateProfile() = AuthorRepository.fetchSelf(this)

    fun updateNotificationCount() = NotificationRepository.fetchSuperNotification(this)

    fun updateInterfaceInformation() {
        updateProfile()
        updateNotificationCount()
    }

    fun setProfile(bio: String) {
        if (bio != userBio.value) {
            AuthorRepository.updateSelfBio(this, bio)
        }

        if (userAvatarNewData.value != null && _userAvatarFileName != null && _userAvatarMimeType != null) {
            AuthorRepository.updateSelfAvatar(
                this,
                _userAvatarFileName!!,
                _userAvatarMimeType!!,
                userAvatarNewData.value!!
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

    companion object {
        val THEMES = arrayOf(
            Constants.Themes.AUTOMATIC,
            Constants.Themes.LIGHT,
            Constants.Themes.DARK
        )

        val NAVIGATION_LINKS = mapOf(
            R.id.about_us to Constants.Links.ABOUT_US,
            R.id.open_source to Constants.Links.OPEN_SOURCE,
            R.id.faq to Constants.Links.FAQ,
            R.id.terms_and_conditions to Constants.Links.TERMS_AND_CONDITIONS,
            R.id.privacy_policy to Constants.Links.PRIVACY_POLICY
        )
    }
}
