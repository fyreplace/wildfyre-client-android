package net.wildfyre.client.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import net.wildfyre.client.data.Post
import net.wildfyre.client.data.repositories.ArchiveRepository

class ArchiveFragmentViewModel(application: Application) : PostsFragmentViewModel(application) {
    override var itemCount: LiveData<Int> = Transformations.map(ArchiveRepository.superPost) { it.count ?: 0 }
    override val items: LiveData<List<Post>> = ArchiveRepository.posts

    override fun fetchNextItems() = ArchiveRepository.fetchNextPosts(this)

    override fun resetItems() = ArchiveRepository.resetPosts()
}
