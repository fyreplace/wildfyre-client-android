package net.wildfyre.client.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import net.wildfyre.client.data.Post
import net.wildfyre.client.data.PostRepository

class ArchiveFragmentViewModel(application: Application) : PostsFragmentViewModel(application) {
    override var itemCount: LiveData<Long> = Transformations.map(PostRepository.superPost) { it.count ?: 0 }
    override val items: LiveData<List<Post>> = PostRepository.posts

    override fun fetchNextItems() = PostRepository.fetchNextPosts(this)

    override fun resetItems() = PostRepository.resetPosts()
}