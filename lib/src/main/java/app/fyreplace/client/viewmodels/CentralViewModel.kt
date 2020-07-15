package app.fyreplace.client.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import app.fyreplace.client.data.models.Author
import app.fyreplace.client.data.models.ImageData
import app.fyreplace.client.data.models.Post
import app.fyreplace.client.data.repositories.AuthRepository
import app.fyreplace.client.data.repositories.AuthorRepository
import app.fyreplace.client.data.repositories.NotificationRepository
import java.text.SimpleDateFormat

class CentralViewModel(
    private val authRepository: AuthRepository,
    private val authorRepository: AuthorRepository,
    private val notificationRepository: NotificationRepository
) : ViewModel() {
    private val mIsLogged = MutableLiveData<Boolean>()
    private val mSelf = MutableLiveData<Author?>()
    private val mNewUserAvatar = MutableLiveData<ImageData?>()
    private val mNotificationCount = MutableLiveData<Int>()
    private val mNotificationBadgeVisible = MutableLiveData<Boolean>()
    private val mPostInfo = MutableLiveData<PostInfo?>()
    private val mAllowDraftCreation = MutableLiveData(true)

    val isLogged: LiveData<Boolean> = mIsLogged
    val self: LiveData<Author?> = mSelf
    val selfId: LiveData<Long> = mSelf.map { it?.user ?: -1 }
    val selfUsername: LiveData<String> = mSelf.map { it?.name.orEmpty() }
    val selfBio: LiveData<String> = mSelf.map { it?.bio.orEmpty() }
    val newUserAvatar: LiveData<ImageData?> = mNewUserAvatar
    val notificationCount: LiveData<Int> = mNotificationCount
    val notificationCountText: LiveData<String> = notificationCount
        .map { if (it < 100) it.toString() else "99" }
    val notificationBadgeVisible: LiveData<Boolean> = mNotificationBadgeVisible
    val postInfo: LiveData<PostInfo?> = mPostInfo
    val allowDraftCreation: LiveData<Boolean> = mAllowDraftCreation

    init {
        if (authRepository.authToken.isNotEmpty()) {
            login()
        } else {
            logout()
        }
    }

    fun login() {
        mIsLogged.value = true
    }

    fun logout() {
        mIsLogged.value = false
        mSelf.value = null
        authRepository.clearAuthToken()
    }

    suspend fun updateProfileInfo() = mSelf.postValue(authorRepository.getSelf())

    suspend fun updateNotificationCount() =
        mNotificationCount.postValue(notificationRepository.getNotificationCount())

    fun forceNotificationCount(count: Int) = mNotificationCount.postValue(count)

    suspend fun sendProfile(bio: String) {
        if (bio != selfBio.value) {
            mSelf.postValue(authorRepository.updateSelfBio(bio))
        }

        newUserAvatar.value?.let { mSelf.postValue(authorRepository.updateSelfAvatar(it)) }
        resetPendingProfileAvatar()
    }

    fun setPendingProfileAvatar(avatar: ImageData) = mNewUserAvatar.postValue(avatar)

    fun resetPendingProfileAvatar() = mNewUserAvatar.postValue(null)

    fun setNotificationBadgeVisible(visible: Boolean) = mNotificationBadgeVisible.postValue(visible)

    fun setPost(post: Post?) = mPostInfo.postValue(post?.let {
        PostInfo(
            it.author,
            DATE_FORMAT.format(it.created)
        )
    })

    fun setAllowDraftCreation(allow: Boolean) = mAllowDraftCreation.postValue(allow)

    companion object {
        private val DATE_FORMAT = SimpleDateFormat.getDateInstance()
    }

    data class PostInfo(
        val author: Author?,
        val date: String
    )
}
