package net.wildfyre.client.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.wildfyre.client.Constants
import net.wildfyre.client.R
import net.wildfyre.client.data.models.Author
import net.wildfyre.client.data.models.Post
import net.wildfyre.client.data.repositories.AuthRepository
import net.wildfyre.client.data.repositories.AuthorRepository
import net.wildfyre.client.data.repositories.NotificationRepository
import net.wildfyre.client.data.repositories.SettingsRepository
import java.text.SimpleDateFormat

class MainActivityViewModel(application: Application) : FailureHandlingViewModel(application) {
    private val _isLogged = MutableLiveData<Boolean>()
    private val _self = MutableLiveData<Author?>()
    private var _userAvatarFileName: String? = null
    private var _userAvatarMimeType: String? = null
    private val _userAvatarNewData = MutableLiveData<ByteArray>()
    private val _notificationCount = MutableLiveData<Int>()
    private val _notificationBadgeVisible = MutableLiveData<Boolean>()
    private var _titleInfo = MutableLiveData<PostInfo?>()

    var startupLogin = true
        private set
    val isLogged: LiveData<Boolean> = _isLogged
    val userId: LiveData<Long> = Transformations.map(_self) { it?.user ?: -1 }
    val userName: LiveData<String> = Transformations.map(_self) { it?.name.orEmpty() }
    val userBio: LiveData<String> = Transformations.map(_self) { it?.bio.orEmpty() }
    val userAvatar: LiveData<String> = Transformations.map(_self) { it?.avatar.orEmpty() }
    val userAvatarNewData: LiveData<ByteArray> = _userAvatarNewData
    val notificationCount: LiveData<Int> = _notificationCount
    val notificationCountText: LiveData<String> =
        Transformations.map(notificationCount) { if (it < 100) it.toString() else "99" }
    val notificationBadgeVisible: LiveData<Boolean> = _notificationBadgeVisible
    val postInfo: LiveData<PostInfo?> = _titleInfo
    val selectedThemeIndex = MutableLiveData<Int>()
    val shouldShowNotificationBadge = MutableLiveData<Boolean>()

    init {
        if (AuthRepository.authToken.isNotEmpty()) {
            _isLogged.value = true
            updateProfileInfoAsync()
        } else {
            _isLogged.value = false
        }

        selectedThemeIndex.value = THEMES.indexOfFirst { it == SettingsRepository.theme }
        selectedThemeIndex.observeForever { SettingsRepository.theme = THEMES[it] }
        shouldShowNotificationBadge.value = SettingsRepository.showBadge
        shouldShowNotificationBadge.observeForever { SettingsRepository.showBadge = it }
    }

    fun login() {
        _isLogged.postValue(true)
        updateProfileInfoAsync()
    }

    fun logout() {
        startupLogin = false
        _isLogged.postValue(false)
        _self.postValue(null)
        AuthRepository.clearAuthToken()
    }

    fun updateNotificationCountAsync() = launchCatching(Dispatchers.IO) {
        _notificationCount.postValue(NotificationRepository.getNotificationCount())
    }

    fun forceNotificationCount(count: Int) = _notificationCount.postValue(count)

    fun setProfileAsync(bio: String) = launchCatching {
        if (bio != userBio.value) {
            _self.postValue(withContext(Dispatchers.IO) { AuthorRepository.updateSelfBio(bio) })
        }

        if (userAvatarNewData.value != null && _userAvatarFileName != null && _userAvatarMimeType != null) {
            withContext(Dispatchers.IO) {
                _self.postValue(
                    AuthorRepository.updateSelfAvatar(
                        _userAvatarFileName!!,
                        _userAvatarMimeType!!,
                        userAvatarNewData.value!!
                    )
                )
            }
        }
    }

    fun setPendingProfileAvatar(fileName: String, mimeType: String, avatar: ByteArray) {
        _userAvatarFileName = fileName
        _userAvatarMimeType = mimeType
        _userAvatarNewData.postValue(avatar)
    }

    fun resetPendingProfileAvatar() {
        _userAvatarFileName = null
        _userAvatarMimeType = null
        _userAvatarNewData.postValue(null)
    }

    fun setNotificationBadgeVisible(visible: Boolean) = _notificationBadgeVisible.postValue(visible)

    fun setPost(post: Post?) = _titleInfo.postValue(post?.let { PostInfo(it.author, DATE_FORMAT.format(it.created)) })

    private fun updateProfileInfoAsync() = launchCatching {
        updateNotificationCountAsync()
        _self.postValue(AuthorRepository.getSelf())
    }

    companion object {
        private val DATE_FORMAT = SimpleDateFormat.getDateInstance()

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

    data class PostInfo(
        val author: Author?,
        val date: String
    )
}
