package app.fyreplace.client.viewmodels

import android.app.Application
import androidx.lifecycle.viewModelScope
import app.fyreplace.client.data.models.Post
import app.fyreplace.client.data.repositories.PostRepository
import kotlinx.coroutines.*

class HomeFragmentViewModel(application: Application) : PostFragmentViewModel(application) {
    private val postReserve: MutableList<Post> = mutableListOf()
    private var postReserveJob: Job? = null
    private var endOfPosts = false
    private var lastAreaName: String? = null
    private var doSpread = true

    init {
        post.observeForever { doSpread = it != null }
    }

    fun nextPostAsync(areaName: String? = null) = viewModelScope.launch {
        if (areaName == lastAreaName) {
            return@launch
        }

        if (areaName != null) {
            lastAreaName = areaName
            postReserveJob?.cancel()
            postReserve.clear()
        }

        if (postReserve.isEmpty()) {
            setPost(null)
            fillReserve()
        }

        if (endOfPosts) {
            return@launch
        }

        if (hasContent.value != true) {
            mHasContent.postValue(true)
        }

        setPost(postReserve.removeAt(0))

        if (postReserve.size <= RESERVE_SIZE / 2) {
            queueJob()
        }
    }

    fun spreadAsync(spread: Boolean) = launchCatching(Dispatchers.IO) {
        if (doSpread) {
            doSpread = false
            PostRepository.spread(postAreaName, postId, spread)
            nextPostAsync().join()
        }
    }

    private suspend fun fillReserve() {
        endOfPosts = false
        postReserveJob?.join()

        while (!endOfPosts && postReserve.isEmpty()) {
            queueJob()
            postReserveJob?.join()
        }
    }

    private fun queueJob() {
        postReserveJob = launchCatching {
            val superPost = withContext(Dispatchers.IO) { PostRepository.getNextPosts(RESERVE_SIZE) }

            if (superPost.count == 0) {
                mHasContent.postValue(false)
                endOfPosts = true
            } else if (isActive) {
                postReserve.addAll(superPost.results.filter { p -> p.id != postId && postReserve.find { it.id == p.id } == null })
            }

            postReserveJob = null
        }
    }

    private companion object {
        const val RESERVE_SIZE = 10
    }
}
