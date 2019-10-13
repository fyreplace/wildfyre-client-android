package app.fyreplace.client.viewmodels

import androidx.lifecycle.viewModelScope
import app.fyreplace.client.data.models.Post
import app.fyreplace.client.data.repositories.AreaRepository
import app.fyreplace.client.data.repositories.CommentRepository
import app.fyreplace.client.data.repositories.PostRepository
import kotlinx.coroutines.*
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

    suspend fun nextPost(areaName: String? = null) {
        if (areaName == lastAreaName) {
            return
        }

        if (areaName != null) {
            setPost(null)
            mHasContent.postValue(true)
            lastAreaName = areaName
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
        viewModelScope.launch(Dispatchers.IO) {
            delay(SPREAD_DELAY)
            mAllowSpread.postValue(contentLoaded.value)
        }

        if (postReserve.size <= RESERVE_SIZE / 2) {
            fetchJob = viewModelScope.launch { fetchPosts() }
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
            postReserve.addAll(superPost.results.filter { p -> p.id != postId && postReserve.find { it.id == p.id } == null })
        }
    }

    private companion object {
        const val RESERVE_SIZE = 10
        const val SPREAD_DELAY = 1000L
    }
}
