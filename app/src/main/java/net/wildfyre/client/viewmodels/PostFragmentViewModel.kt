package net.wildfyre.client.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import net.wildfyre.client.data.Comment
import net.wildfyre.client.data.CommentRepository
import net.wildfyre.client.data.Post
import net.wildfyre.client.data.PostRepository
import net.wildfyre.client.views.markdown.prepareForMarkdown

class PostFragmentViewModel(application: Application) : FailureHandlingViewModel(application) {
    private var _postAreaName: String? = null
    private val _postId = MutableLiveData<Long>()
    private val _comments = MediatorLiveData<List<Comment>>()

    val post: LiveData<Post> = Transformations.switchMap(_postId) { PostRepository.getPost(this, _postAreaName, it) }
    val contentLoaded: LiveData<Boolean> = Transformations.map(post) { it != null }
    val markdownContent: LiveData<String> = Transformations.map(post) {
        val markdownContent = StringBuilder()
        it.image?.run { markdownContent.append("![]($this)\n\n") }
        it.text?.run { markdownContent.append(prepareForMarkdown(it.additional_images)) }
        markdownContent.toString()
    }
    val comments: LiveData<List<Comment>> = _comments
    val commentCount: LiveData<Int> = Transformations.map(comments) { it?.size ?: 0 }
    val newCommentData = MutableLiveData<String>()

    init {
        _comments.addSource(post) { _comments.value = it.comments ?: listOf() }
        newCommentData.value = ""
    }

    fun setPostData(areaName: String?, id: Long) {
        if (_postId.value != id) {
            _postAreaName = areaName
            _postId.value = id
        }
    }

    fun sendComment() {
        if (newCommentData.value != null && _postId.value != null) {
            val futureComment = CommentRepository.sendComment(
                this,
                _postAreaName,
                _postId.value!!,
                newCommentData.value!!
            )

            _comments.addSource(futureComment) {
                _comments.removeSource(futureComment)
                _comments.value = (_comments.value ?: listOf()) + it
                newCommentData.value = ""
            }
        }
    }
}
