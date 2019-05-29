package net.wildfyre.client.data.repositories

import androidx.lifecycle.LiveData
import net.wildfyre.client.data.FailureHandler
import net.wildfyre.client.data.Post
import net.wildfyre.client.data.Services
import net.wildfyre.client.data.SuperPost

object OwnPostRepository {
    private val delegate = AccumulatorRepositoryDelegate<Post>()

    val superPost: LiveData<SuperPost> = delegate.mutableSuperItem
    val posts: LiveData<List<Post>> = delegate.mutableItems

    fun fetchNextPosts(fh: FailureHandler) {
        val call = Services.webService.getOwnPosts(
            AuthRepository.authToken.value!!,
            AreaRepository.preferredAreaName.value.orEmpty(),
            AccumulatorRepositoryDelegate.BUCKET_SIZE,
            delegate.offset
        )

        delegate.fetchNextItems(call, fh, true)
    }

    fun resetPosts() = delegate.resetItems()
}

