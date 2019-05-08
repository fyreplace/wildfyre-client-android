package net.wildfyre.client.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import net.wildfyre.client.data.Post
import net.wildfyre.client.data.PostRepository

class PostFragmentViewModel(application: Application) : FailureHandlingViewModel(application) {
    private val _postId = MutableLiveData<Long>()

    val post: LiveData<Post> = Transformations.switchMap(_postId) { PostRepository.getPost(this, it) }

    fun setPostId(id: Long) {
        _postId.value = id
    }
}