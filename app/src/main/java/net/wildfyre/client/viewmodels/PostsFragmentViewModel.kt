package net.wildfyre.client.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import net.wildfyre.client.data.OwnPostsRepository
import net.wildfyre.client.data.Post

class PostsFragmentViewModel(application: Application) : FailureHandlingViewModel(application),
    ItemsListViewModel<Post> {
    override var itemCount: LiveData<Long> = Transformations.map(OwnPostsRepository.superPost) { it.count ?: 0 }
    override val items: LiveData<List<Post>> = OwnPostsRepository.posts

    override fun fetchNextItems() = OwnPostsRepository.fetchNextPosts(this)

    override fun resetItems() = OwnPostsRepository.resetPosts()
}