package net.wildfyre.client.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import net.wildfyre.client.data.Comment
import net.wildfyre.client.data.Post
import net.wildfyre.client.data.SingleLiveEvent
import net.wildfyre.client.data.repositories.AuthorRepository
import net.wildfyre.client.data.repositories.CommentRepository
import net.wildfyre.client.data.repositories.PostRepository
import net.wildfyre.client.ui.prepareForMarkdown

class PostFragmentViewModel(application: Application) : FailureHandlingViewModel(application) {
    private var _postAreaName: String? = null
    private val _postId = MutableLiveData<Long>()
    private val _commentAddedEvent = SingleLiveEvent<Comment>()
    private val _commentRemovedEvent = SingleLiveEvent<Int>()

    val selfId: LiveData<Long> = Transformations.map(AuthorRepository.self) { it.user }
    val post: LiveData<Post> = Transformations.switchMap(_postId) { PostRepository.getPost(this, _postAreaName, it) }
    val contentLoaded: LiveData<Boolean> = Transformations.map(post) { it != null }
    val markdownContent: LiveData<String> = Transformations.map(post) {
        val markdownContent = StringBuilder()
        it.image?.run { markdownContent.append("![]($this)\n\n") }
        it.text?.run { markdownContent.append(prepareForMarkdown(it.additionalImages)) }
        markdownContent.toString()
    }
    val comments: LiveData<List<Comment>> = Transformations.map(post) { it.comments }
    val commentAddedEvent: LiveData<Comment> = _commentAddedEvent
    val commentRemovedEvent: LiveData<Int> = _commentRemovedEvent
    val commentCount: LiveData<Int> = Transformations.map(comments) { it?.size ?: 0 }
    val newCommentData = MutableLiveData<String>()

    init {
        newCommentData.value = ""
    }

    fun setPostData(areaName: String?, id: Long) {
        if (_postId.value != id) {
            _postAreaName = areaName
            _postId.value = id
        }
    }

    fun sendNewComment() {
        if (newCommentData.value != null && _postId.value != null) {
            CommentRepository.sendComment(
                this,
                _postAreaName,
                _postId.value!!,
                newCommentData.value!!
            ) {
                _commentAddedEvent.value = it
                newCommentData.value = ""
            }
        }
    }

    fun deleteComment(position: Int, comment: Comment) {
        _postId.value?.let {
            CommentRepository.deleteComment(this, _postAreaName, it, comment.id ?: -1) {
                _commentRemovedEvent.value = position
            }
        }
    }
}
