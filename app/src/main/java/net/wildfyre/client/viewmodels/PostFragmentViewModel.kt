package net.wildfyre.client.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
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
    private val _commentCount = MediatorLiveData<Int>()

    val post: LiveData<Post> = Transformations.switchMap(_postId) { PostRepository.getPost(this, _postAreaName, it) }
    val contentLoaded: LiveData<Boolean> = Transformations.map(post) { it != null }
    val selfId: LiveData<Long> = Transformations.map(AuthorRepository.self) { it.user }
    val authorId: LiveData<Long> = Transformations.map(post) { it.author?.user ?: -1 }
    val markdownContent: LiveData<String> = Transformations.map(post) {
        val markdownContent = StringBuilder()
        it.image?.run { markdownContent.append("![]($this)\n\n") }
        it.text?.run { markdownContent.append(prepareForMarkdown(it.additionalImages)) }
        return@map markdownContent.toString()
    }
    val comments: LiveData<List<Comment>> = Transformations.map(post) { it.comments }
    val commentAddedEvent: LiveData<Comment> = _commentAddedEvent
    val commentRemovedEvent: LiveData<Int> = _commentRemovedEvent
    val commentCount: LiveData<Int> = _commentCount
    val newCommentData = MutableLiveData<String>()

    init {
        _commentCount.addSource(comments) { _commentCount.value = it.size }
        _commentCount.addSource(commentAddedEvent) { _commentCount.value = _commentCount.value!! + 1 }
        _commentCount.addSource(commentRemovedEvent) { _commentCount.value = _commentCount.value!! - 1 }
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
            CommentRepository.deleteComment(this, _postAreaName, it, comment.id) {
                _commentRemovedEvent.value = position
            }
        }
    }
}
