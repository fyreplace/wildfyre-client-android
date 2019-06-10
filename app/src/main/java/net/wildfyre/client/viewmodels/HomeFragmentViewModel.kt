package net.wildfyre.client.viewmodels

import android.app.Application
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext
import net.wildfyre.client.data.Post
import net.wildfyre.client.data.repositories.PostRepository

class HomeFragmentViewModel(application: Application) : PostFragmentViewModel(application) {
    private val postReserve: MutableList<Post> = mutableListOf()
    private var postReserveJob: Job? = null
    private var endOfPosts = false

    fun nextPostAsync(areaName: String? = null) = launchCatching {
        if (areaName != null) {
            if (areaName == postAreaName) {
                return@launchCatching
            }

            postReserve.clear()
        }

        if (postReserve.isEmpty()) {
            setPostAsync(null)
            fillReserve()
        }

        if (endOfPosts) {
            return@launchCatching
        }

        setPostAsync(postReserve.removeAt(0))

        if (postReserve.size <= RESERVE_SIZE / 2) {
            queueJob()
        }
    }

    fun spreadAsync(spread: Boolean) = launchCatching(Dispatchers.IO) {
        PostRepository.spread(postAreaName, postId, spread)
        nextPostAsync().join()
    }

    private suspend fun fillReserve() {
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
                endOfPosts = true
            } else {
                postReserve.addAll(superPost.results)
            }

            postReserveJob = null
        }
    }

    private companion object {
        const val RESERVE_SIZE = 6
    }
}
