package app.fyreplace.client.viewmodels

import androidx.lifecycle.*
import app.fyreplace.client.Constants
import app.fyreplace.client.R
import app.fyreplace.client.data.models.Author
import app.fyreplace.client.data.models.Post
import app.fyreplace.client.data.repositories.AuthRepository
import app.fyreplace.client.data.repositories.AuthorRepository
import app.fyreplace.client.data.repositories.NotificationRepository
import app.fyreplace.client.data.repositories.SettingsRepository
import kotlinx.coroutines.*
import java.text.SimpleDateFormat

class MainActivityViewModel : ViewModel() {
    private var uiRefreshTickerJob: Job? = null
    private val mUiRefreshTick = MutableLiveData<Unit>()
    private val mIsLogged = MutableLiveData<Boolean>()
    private val mSelf = MutableLiveData<Author?>()
    private val mUserAvatarNewData = MutableLiveData<ByteArray>()
    private val mNotificationCount = MutableLiveData<Int>()
    private val mNotificationBadgeVisible = MutableLiveData<Boolean>()
    private val mPostInfo = MutableLiveData<PostInfo?>()
    private var userAvatarFileName: String? = null
    private var userAvatarMimeType: String? = null

    val uiRefreshTick: LiveData<Unit> = mUiRefreshTick
    var startupLogin = true
        private set
    val isLogged: LiveData<Boolean> = mIsLogged
    val userId: LiveData<Long> = mSelf.map { it?.user ?: -1 }
    val userName: LiveData<String> = mSelf.map { it?.name.orEmpty() }
    val userBio: LiveData<String> = mSelf.map { it?.bio.orEmpty() }
    val userAvatar: LiveData<String> = mSelf.map { it?.avatar.orEmpty() }
    val userAvatarNewData: LiveData<ByteArray> = mUserAvatarNewData
    val notificationCount: LiveData<Int> = mNotificationCount
    val notificationCountText: LiveData<String> = notificationCount.map { if (it < 100) it.toString() else "99" }
    val notificationBadgeVisible: LiveData<Boolean> = mNotificationBadgeVisible
    val postInfo: LiveData<PostInfo?> = mPostInfo
    val selectedThemeIndex = MutableLiveData<Int>()
    val shouldShowNotificationBadge = MutableLiveData<Boolean>()

    init {
        if (AuthRepository.authToken.isNotEmpty()) {
            login()
        } else {
            mIsLogged.value = false
        }

        selectedThemeIndex.value = THEMES.indexOfFirst { it == SettingsRepository.theme }
        selectedThemeIndex.observeForever { SettingsRepository.theme = THEMES[it] }
        shouldShowNotificationBadge.value = SettingsRepository.showBadge
        shouldShowNotificationBadge.observeForever { SettingsRepository.showBadge = it }
    }

    fun login() {
        uiRefreshTickerJob = viewModelScope.launch {
            while (true) {
                delay(UI_UPDATE_MILLIS)
                mUiRefreshTick.postValue(Unit)
            }
        }
        mIsLogged.value = true
        viewModelScope.launch { updateProfileInfo() }
    }

    fun logout() {
        startupLogin = false
        mIsLogged.value = false
        mSelf.value = null
        uiRefreshTickerJob?.cancel()
        AuthRepository.clearAuthToken()
    }

    suspend fun updateNotificationCount() = withContext(Dispatchers.IO) {
        mNotificationCount.postValue(NotificationRepository.getNotificationCount())
    }

    fun forceNotificationCount(count: Int) = mNotificationCount.postValue(count)

    suspend fun setProfile(bio: String) {
        if (bio != userBio.value) {
            mSelf.postValue(withContext(Dispatchers.IO) { AuthorRepository.updateSelfBio(bio) })
        }

        if (userAvatarNewData.value != null && userAvatarFileName != null && userAvatarMimeType != null) {
            withContext(Dispatchers.IO) {
                mSelf.postValue(
                    AuthorRepository.updateSelfAvatar(
                        userAvatarFileName!!,
                        userAvatarMimeType!!,
                        userAvatarNewData.value!!
                    )
                )
            }
        }
    }

    fun setPendingProfileAvatar(fileName: String, mimeType: String, avatar: ByteArray) {
        userAvatarFileName = fileName
        userAvatarMimeType = mimeType
        mUserAvatarNewData.postValue(avatar)
    }

    fun resetPendingProfileAvatar() {
        userAvatarFileName = null
        userAvatarMimeType = null
        mUserAvatarNewData.postValue(null)
    }

    fun setNotificationBadgeVisible(visible: Boolean) = mNotificationBadgeVisible.postValue(visible)

    fun setPost(post: Post?) = mPostInfo.postValue(post?.let { PostInfo(it.author, DATE_FORMAT.format(it.created)) })

    private suspend fun updateProfileInfo() {
        updateNotificationCount()
        mSelf.postValue(AuthorRepository.getSelf())
    }

    companion object {
        private const val UI_UPDATE_MILLIS = 10_000L
        private val DATE_FORMAT = SimpleDateFormat.getDateInstance()

        val THEMES = arrayOf(
            Constants.Themes.AUTOMATIC,
            Constants.Themes.LIGHT,
            Constants.Themes.DARK
        )

        val NAVIGATION_LINKS = mapOf(
            R.id.fyreplace_website to Constants.Links.Fyreplace.WEBSITE,
            R.id.fyreplace_open_source to Constants.Links.Fyreplace.OPEN_SOURCE,
            R.id.wildfyre_website to Constants.Links.WildFyre.WEBSITE,
            R.id.wildfyre_open_source to Constants.Links.WildFyre.OPEN_SOURCE,
            R.id.wildfyre_faq to Constants.Links.WildFyre.FAQ,
            R.id.wildfyre_terms_and_conditions to Constants.Links.WildFyre.TERMS_AND_CONDITIONS,
            R.id.wildfyre_privacy_policy to Constants.Links.WildFyre.PRIVACY_POLICY
        )
    }

    data class PostInfo(
        val author: Author?,
        val date: String
    )
}
