package net.wildfyre.client.data

import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import net.wildfyre.client.Constants
import net.wildfyre.client.R
import net.wildfyre.client.WildFyreApplication
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call

object SettingsRepository {
    private val mutableTheme = MutableLiveData<Int>()
    private val mutableBadgeToggle = MutableLiveData<Boolean>()

    val theme: LiveData<Int> = mutableTheme
    val badgeToggle: LiveData<Boolean> = mutableBadgeToggle

    init {
        mutableTheme.value =
            WildFyreApplication.preferences.getInt(Constants.Preferences.UI_THEME, Constants.Themes.AUTOMATIC)
        mutableBadgeToggle.value = WildFyreApplication.preferences.getBoolean(Constants.Preferences.UI_BADGE, true)
    }

    fun setTheme(theme: Int) {
        WildFyreApplication.preferences.edit { putInt(Constants.Preferences.UI_THEME, theme) }
        mutableTheme.value = theme
    }

    fun toggleBadge(show: Boolean) {
        WildFyreApplication.preferences.edit { putBoolean(Constants.Preferences.UI_BADGE, show) }
        mutableBadgeToggle.value = show
    }
}

object AuthRepository {
    private val mutableAuthToken = MutableLiveData<String>()

    val authToken: LiveData<String> = mutableAuthToken

    init {
        mutableAuthToken.value = WildFyreApplication.preferences.getString(Constants.Preferences.AUTH_TOKEN, "")
    }

    fun clearAuthToken() = setAuthToken("")

    fun fetchAuthToken(fh: FailureHandler, username: String, password: String) {
        val auth = Auth().also { it.username = username; it.password = password }

        Services.webService.postAuth(auth).then(fh, R.string.failure_login) {
            if (it.token != null) {
                setAuthToken("token " + it.token)
            }
        }
    }

    private fun setAuthToken(token: String) {
        mutableAuthToken.value = token
        WildFyreApplication.preferences.edit { putString(Constants.Preferences.AUTH_TOKEN, token) }
    }
}

object AuthorRepository {
    private val mutableSelf = MutableLiveData<Author>()

    val self: LiveData<Author> = mutableSelf

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

object AreaRepository {
    private val mutableAreas = MutableLiveData<List<Area>>()
    private val mutablePreferredAreaName = MutableLiveData<String>()
    private val mutablePreferredAreaReputation = MutableLiveData<Reputation>()

    val areas: LiveData<List<Area>> = mutableAreas
    val preferredAreaName: LiveData<String> = mutablePreferredAreaName
    val preferredAreaReputation: LiveData<Reputation> = mutablePreferredAreaReputation

    init {
        WildFyreApplication.preferences.getString(Constants.Preferences.AREA_PREFERRED, null)
            ?.let { mutablePreferredAreaName.value = it }
    }

    fun fetchAreas(fh: FailureHandler) =
        Services.webService.getAreas(AuthRepository.authToken.value!!)
            .then(fh, R.string.failure_request) { mutableAreas.value = it }

    fun fetchAreaReputation(fh: FailureHandler, areaName: String) =
        Services.webService.getAreaRep(AuthRepository.authToken.value!!, areaName)
            .then(fh, R.string.failure_request) { mutablePreferredAreaReputation.value = it }

    fun setPreferredAreaName(name: String) {
        if (name != preferredAreaName.value) {
            WildFyreApplication.preferences.edit { putString(Constants.Preferences.AREA_PREFERRED, name) }
            mutablePreferredAreaName.value = name
        }
    }
}

object NotificationRepository {
    private val delegate = AccumulatorRepositoryDelegate<Notification>()

    val superNotification: LiveData<SuperNotification> = delegate.mutableSuperItem
    val notifications: LiveData<List<Notification>> = delegate.mutableItems

    fun fetchNextNotifications(fh: FailureHandler, forContent: Boolean) {
        val call = Services.webService.getNotifications(
            AuthRepository.authToken.value!!,
            if (forContent) AccumulatorRepositoryDelegate.BUCKET_SIZE else 1,
            delegate.offset
        )

        delegate.fetchNextItems(call, fh, forContent)
    }

    fun resetNotifications() = delegate.resetItems()

    fun removeNotification(fh: FailureHandler, id: Long) =
        notifications.value!!.firstOrNull { it.post?.id == id }?.let {
            delegate.removeItem(it)
            fetchNextNotifications(fh, false)
        }

    fun clearNotifications(fh: FailureHandler) =
        Services.webService.deleteNotifications(AuthRepository.authToken.value!!)
            .then(fh, R.string.failure_request) { resetNotifications() }
}

object PostRepository {
    fun getPost(fh: FailureHandler, id: Long): LiveData<Post> {
        val post = MutableLiveData<Post>()

        if (id >= 0) {
            Services.webService.getPost(AreaRepository.preferredAreaName.value ?: "", id)
                .then(fh, R.string.failure_request) {
                    post.value = it
                    NotificationRepository.removeNotification(fh, it.id!!)
                }
        }

        return post
    }
}

object ArchiveRepository {
    private val delegate = AccumulatorRepositoryDelegate<Post>()

    val superPost: LiveData<SuperPost> = delegate.mutableSuperItem
    val posts: LiveData<List<Post>> = delegate.mutableItems

    fun fetchNextPosts(fh: FailureHandler) {
        val call = Services.webService.getPosts(
            AuthRepository.authToken.value!!,
            AreaRepository.preferredAreaName.value ?: "",
            AccumulatorRepositoryDelegate.BUCKET_SIZE,
            delegate.offset
        )

        delegate.fetchNextItems(call, fh, true)
    }

    fun resetPosts() = delegate.resetItems()
}

object OwnPostRepository {
    private val delegate = AccumulatorRepositoryDelegate<Post>()

    val superPost: LiveData<SuperPost> = delegate.mutableSuperItem
    val posts: LiveData<List<Post>> = delegate.mutableItems

    fun fetchNextPosts(fh: FailureHandler) {
        val call = Services.webService.getOwnPosts(
            AuthRepository.authToken.value!!,
            AreaRepository.preferredAreaName.value ?: "",
            AccumulatorRepositoryDelegate.BUCKET_SIZE,
            delegate.offset
        )

        delegate.fetchNextItems(call, fh, true)
    }

    fun resetPosts() = delegate.resetItems()
}

private class AccumulatorRepositoryDelegate<T> {
    internal val mutableSuperItem = MutableLiveData<SuperItem<T>>()
    internal val mutableItems = MutableLiveData<List<T>>()
    internal var offset = 0L
    internal var fetchingContent = false

    init {
        resetItems()
    }

    fun fetchNextItems(call: Call<SuperItem<T>>, fh: FailureHandler, forContent: Boolean) {
        when {
            fetchingContent -> return
            forContent -> fetchingContent = true
        }

        call.then(fh, R.string.failure_request) {
            fetchingContent = false
            mutableSuperItem.value = it

            if (forContent) {
                it.results?.run {
                    offset += size
                    mutableItems.value = mutableItems.value!! + this
                }
            }
        }
    }

    fun removeItem(item: T) {
        mutableSuperItem.value = mutableSuperItem.value?.apply { count = count!! - 1 }
        mutableItems.value = mutableItems.value?.subtract(setOf(item))?.toList()
    }

    fun resetItems() {
        offset = 0
        mutableSuperItem.value = (mutableSuperItem.value ?: SuperItem()).apply { count = 0; results = listOf() }
        mutableItems.value = listOf()
    }

    companion object {
        const val BUCKET_SIZE = 24L
    }
}
