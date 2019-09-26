package app.fyreplace.client.viewmodels

import androidx.lifecycle.*
import app.fyreplace.client.Constants
import app.fyreplace.client.R
import app.fyreplace.client.data.models.Author
import app.fyreplace.client.data.models.ImageData
import app.fyreplace.client.data.models.Post
import app.fyreplace.client.data.repositories.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat

class MainActivityViewModel(
    private val settingsRepository: SettingsRepository,
    private val authRepository: AuthRepository,
    private val authorRepository: AuthorRepository,
    private val notificationRepository: NotificationRepository,
    private val draftRepository: DraftRepository
) : ViewModel() {
    private var uiRefreshTickerJob: Job? = null
    private val mUiRefreshTick = MutableLiveData<Unit>()
    private val mIsLogged = MutableLiveData<Boolean>()
    private val mSelf = MutableLiveData<Author?>()
    private val mNewUserAvatar = MutableLiveData<ImageData?>()
    private val mNotificationCount = MutableLiveData<Int>()
    private val mNotificationBadgeVisible = MutableLiveData<Boolean>()
    private val mPostInfo = MutableLiveData<PostInfo?>()
    private val mAllowDraftCreation = MutableLiveData<Boolean>()

    val uiRefreshTick: LiveData<Unit> = mUiRefreshTick
    var startupLogin = true
        private set
    val isLogged: LiveData<Boolean> = mIsLogged
    val userId: LiveData<Long> = mSelf.map { it?.user ?: -1 }
    val userName: LiveData<String> = mSelf.map { it?.name.orEmpty() }
    val userBio: LiveData<String> = mSelf.map { it?.bio.orEmpty() }
    val userAvatar: LiveData<String> = mSelf.map { it?.avatar.orEmpty() }
    val newUserAvatar: LiveData<ImageData?> = mNewUserAvatar
    val notificationCount: LiveData<Int> = mNotificationCount
    val notificationCountText: LiveData<String> = notificationCount
        .map { if (it < 100) it.toString() else "99" }
    val notificationBadgeVisible: LiveData<Boolean> = mNotificationBadgeVisible
    val postInfo: LiveData<PostInfo?> = mPostInfo
    val allowDraftCreation: LiveData<Boolean> = mAllowDraftCreation
    val selectedThemeIndex = MutableLiveData<Int>()
    val shouldShowNotificationBadge = MutableLiveData<Boolean>()

    init {
        if (authRepository.authToken.isNotEmpty()) {
            login()
        } else {
            mIsLogged.value = false
        }

        selectedThemeIndex.value = THEMES.indexOfFirst { it == settingsRepository.theme }
        selectedThemeIndex.observeForever { settingsRepository.theme = getTheme(it) }
        shouldShowNotificationBadge.value = settingsRepository.showBadge
        shouldShowNotificationBadge.observeForever { settingsRepository.showBadge = it }
        setAllowDraftCreation(true)
    }

    fun getTheme(which: Int) = if (which in THEMES) THEMES[which] else Constants.Themes.AUTOMATIC

    fun login() {
        uiRefreshTickerJob = viewModelScope.launch {
            while (true) {
                delay(UI_UPDATE_MILLIS)
                mUiRefreshTick.postValue(Unit)
            }
        }
        mIsLogged.value = true
    }

    fun logout() {
        startupLogin = false
        mIsLogged.value = false
        mSelf.value = null
        uiRefreshTickerJob?.cancel()
        authRepository.clearAuthToken()
    }

    suspend fun updateProfileInfo() {
        updateNotificationCount()
        mSelf.postValue(authorRepository.getSelf())
    }

    suspend fun updateNotificationCount() =
        mNotificationCount.postValue(notificationRepository.getNotificationCount())

    fun forceNotificationCount(count: Int) = mNotificationCount.postValue(count)

    suspend fun sendProfile(bio: String) {
        if (bio != userBio.value) {
            mSelf.postValue(authorRepository.updateSelfBio(bio))
        }

        newUserAvatar.value?.let { mSelf.postValue(authorRepository.updateSelfAvatar(it)) }
        resetPendingProfileAvatar()
    }

    fun setPendingProfileAvatar(avatar: ImageData) = mNewUserAvatar.postValue(avatar)

    fun resetPendingProfileAvatar() = mNewUserAvatar.postValue(null)

    fun setNotificationBadgeVisible(visible: Boolean) = mNotificationBadgeVisible.postValue(visible)

    fun setPost(post: Post?) =
        mPostInfo.postValue(post?.let { PostInfo(it.author, DATE_FORMAT.format(it.created)) })

    fun setAllowDraftCreation(allow: Boolean) = mAllowDraftCreation.postValue(allow)

    suspend fun createDraft() = draftRepository.createDraft()

    companion object {
        private const val UI_UPDATE_MILLIS = 10_000L
        private val DATE_FORMAT = SimpleDateFormat.getDateInstance()

        val THEMES = arrayOf(
            Constants.Themes.AUTOMATIC,
            Constants.Themes.LIGHT,
            Constants.Themes.DARK
        )

        val NAVIGATION_LINKS = mapOf(
            R.id.fyreplace_website to R.string.main_nav_fyreplace_website_link,
            R.id.fyreplace_open_source to R.string.main_nav_fyreplace_open_source_link,
            R.id.wildfyre_website to R.string.main_nav_wildfyre_website_link,
            R.id.wildfyre_open_source to R.string.main_nav_wildfyre_open_source_link,
            R.id.wildfyre_faq to R.string.main_nav_wildfyre_faq_link,
            R.id.wildfyre_terms_and_conditions to R.string.main_nav_wildfyre_terms_link,
            R.id.wildfyre_privacy_policy to R.string.main_nav_wildfyre_privacy_link
        )
    }

    data class PostInfo(
        val author: Author?,
        val date: String
    )
}
