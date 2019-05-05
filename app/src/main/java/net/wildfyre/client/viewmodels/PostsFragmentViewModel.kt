package net.wildfyre.client.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import net.wildfyre.client.data.OwnPostRepository
import net.wildfyre.client.data.Post

class PostsFragmentViewModel(application: Application) : FailureHandlingViewModel(application),
    ItemsListViewModel<Post> {
    override var itemCount: LiveData<Long> = Transformations.map(OwnPostRepository.superPost) { it.count ?: 0 }
    override val items: LiveData<List<Post>> = OwnPostRepository.posts

    override fun fetchNextItems() = OwnPostRepository.fetchNextPosts(this)

    override fun resetItems() = OwnPostRepository.resetPosts()
}