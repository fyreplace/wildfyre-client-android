package net.wildfyre.client.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import net.wildfyre.client.data.Post
import net.wildfyre.client.data.PostRepository
import net.wildfyre.client.views.markdown.prepareForMarkdown

class PostFragmentViewModel(application: Application) : FailureHandlingViewModel(application) {
    private val _postId = MutableLiveData<Long>()

    val post: LiveData<Post> = Transformations.switchMap(_postId) { PostRepository.getPost(this, it) }
    val markdownContent: LiveData<String> = Transformations.map(post) {
        val markdownContent = StringBuilder()
        it.image?.run { markdownContent.append("![]($this)\n\n") }
        it.text?.run { markdownContent.append(prepareForMarkdown(it.additional_images)) }
        markdownContent.toString()
    }
    val commentCount: LiveData<Int> = Transformations.map(post) { it.comments?.size ?: 0 }

    fun setPostId(id: Long) {
        if (_postId.value != id) {
            _postId.value = id
        }
    }
}