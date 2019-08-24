package app.fyreplace.client.viewmodels

import androidx.lifecycle.viewModelScope
import app.fyreplace.client.data.models.Post
import app.fyreplace.client.data.repositories.PostRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext

class HomeFragmentViewModel : PostFragmentViewModel() {
    private val postReserve: MutableList<Post> = mutableListOf()
    private var fetchJob: Job? = null
    private var endOfPosts = false
    private var lastAreaName: String? = null
    private var doSpread = true

    init {
        post.observeForever { doSpread = it != null }
    }

    suspend fun nextPost(areaName: String? = null) {
        if (areaName == lastAreaName) {
            return
        }

        if (areaName != null) {
            lastAreaName = areaName
            fetchJob?.cancel()
            postReserve.clear()
        }

        if (postReserve.isEmpty()) {
            setPost(null)
            fillReserve()
        }

        if (endOfPosts) {
            return
        }

        if (hasContent.value != true) {
            mHasContent.postValue(true)
        }

        setPost(postReserve.removeAt(0))

        if (postReserve.size <= RESERVE_SIZE / 2) {
            fetchJob = viewModelScope.launch { fetchPosts() }
        }
    }

    suspend fun spread(spread: Boolean) {
        if (doSpread) {
            doSpread = false
            delay(SPREAD_DELAY)
            PostRepository.spread(postAreaName, postId, spread)
            nextPost()
        }
    }

    private suspend fun fillReserve() {
        endOfPosts = false
        fetchJob?.join()

        while (!endOfPosts && postReserve.isEmpty()) {
            fetchPosts()
        }
    }

    private suspend fun fetchPosts() {
        val superPost = PostRepository.getNextPosts(RESERVE_SIZE)

        if (superPost.count == 0) {
            mHasContent.postValue(false)
            endOfPosts = true
        } else if (coroutineContext.isActive) {
            postReserve.addAll(superPost.results.filter { p -> p.id != postId && postReserve.find { it.id == p.id } == null })
        }
    }

    private companion object {
        const val RESERVE_SIZE = 10
        const val SPREAD_DELAY = 300L
    }
}
