package app.fyreplace.client.viewmodels

import androidx.lifecycle.viewModelScope
import app.fyreplace.client.data.models.Post
import app.fyreplace.client.data.repositories.AreaRepository
import app.fyreplace.client.data.repositories.CommentRepository
import app.fyreplace.client.data.repositories.PostRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext

class HomeFragmentViewModel(
    areaRepository: AreaRepository,
    commentRepository: CommentRepository,
    private val postRepository: PostRepository
) : PostFragmentViewModel(areaRepository, commentRepository, postRepository) {
    private val postReserve: MutableList<Post> = mutableListOf()
    private var fetchJob: Job? = null
    private var endOfPosts = false
    private var lastAreaName: String? = null

    suspend fun nextPost(areaName: String? = null, refill: Boolean = false) {
        if (areaName == lastAreaName) {
            return
        }

        val forcedArea = areaName != null

        if (forcedArea || (refill && endOfPosts)) {
            if (forcedArea) {
                lastAreaName = areaName
            }

            setPost(null)
            mHasContent.postValue(true)
            fetchJob?.cancel()
            postReserve.clear()
        }

        if (postReserve.isEmpty()) {
            setPost(null)
            fillReserve()
        }

        if (endOfPosts) {
            setPost(null)
            return
        }

        setPost(postReserve.removeAt(0))

        if (postReserve.isEmpty()) {
            fetchJob = viewModelScope.launch { fetchPosts() }
        }

        viewModelScope.launch {
            delay(SPREAD_DELAY)
            mAllowSpread.postValue(contentLoaded.value)
        }
    }

    suspend fun spread(spread: Boolean) {
        mAllowSpread.postValue(false)

        try {
            postRepository.spread(postAreaName, postId, spread)
        } catch (e: Exception) {
            mAllowSpread.postValue(true)
            throw e
        }

        nextPost()
    }

    private suspend fun fillReserve() {
        endOfPosts = false
        fetchJob?.join()

        while (!endOfPosts && postReserve.isEmpty()) {
            fetchPosts()
        }
    }

    private suspend fun fetchPosts() {
        val superPost = postRepository.getNextPosts(RESERVE_SIZE)

        if (superPost.count == 0) {
            mHasContent.postValue(false)
            endOfPosts = true
        } else if (coroutineContext.isActive) {
            mHasContent.postValue(true)
            postReserve.addAll(superPost.results.filter { p -> p.id != postId })
        }
    }

    private companion object {
        const val RESERVE_SIZE = 10
        const val SPREAD_DELAY = 1000L
    }
}
