package net.wildfyre.client.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import net.wildfyre.client.data.ArchiveRepository
import net.wildfyre.client.data.Post

class ArchiveFragmentViewModel(application: Application) : PostsFragmentViewModel(application) {
    override var itemCount: LiveData<Long> = Transformations.map(ArchiveRepository.superPost) { it.count ?: 0 }
    override val items: LiveData<List<Post>> = ArchiveRepository.posts

    override fun fetchNextItems() = ArchiveRepository.fetchNextPosts(this)

    override fun resetItems() = ArchiveRepository.resetPosts()
}