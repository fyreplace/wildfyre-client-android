package net.wildfyre.client.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.wildfyre.client.data.Comment
import net.wildfyre.client.data.Post
import net.wildfyre.client.data.SingleLiveEvent
import net.wildfyre.client.data.repositories.AuthorRepository
import net.wildfyre.client.data.repositories.CommentRepository
import net.wildfyre.client.data.repositories.PostRepository
import net.wildfyre.client.ui.prepareForMarkdown

open class PostFragmentViewModel(application: Application) : FailureHandlingViewModel(application) {
    protected val _post = MutableLiveData<Post>()
    private var _postAreaName: String? = null
    private var _postId: Long = -1
    private val _markdownContent = MediatorLiveData<String>()
    private val _commentAddedEvent = SingleLiveEvent<Comment>()
    private val _commentRemovedEvent = SingleLiveEvent<Int>()
    private val _commentCount = MediatorLiveData<Int>()

    val post: LiveData<Post?> = _post
    val contentLoaded: LiveData<Boolean> = Transformations.map(post) { it != null }
    val selfId: LiveData<Long> = Transformations.map(AuthorRepository.self) { it.user }
    val authorId: LiveData<Long> = Transformations.map(post) { it?.author?.user ?: -1 }
    val markdownContent: LiveData<String> = _markdownContent
    val comments: LiveData<List<Comment>> = Transformations.map(post) { it?.comments ?: emptyList() }
    val commentAddedEvent: LiveData<Comment> = _commentAddedEvent
    val commentRemovedEvent: LiveData<Int> = _commentRemovedEvent
    val commentCount: LiveData<Int> = _commentCount
    val newCommentData = MutableLiveData<String>()

    init {
        _markdownContent.addSource(post) {
            launchCatching(Dispatchers.Default) {
                val markdownContent = StringBuilder()
                it?.image?.run { markdownContent.append("![]($this)\n\n") }
                it?.text?.run {
                    markdownContent.append(it.additionalImages
                        ?.let { images -> prepareForMarkdown(images) } ?: this)
                }
                _markdownContent.postValue(markdownContent.toString())
            }
        }

        _commentCount.addSource(comments) { _commentCount.postValue(it.size) }
        _commentCount.addSource(commentAddedEvent) { _commentCount.postValue(_commentCount.value!! + 1) }
        _commentCount.addSource(commentRemovedEvent) { _commentCount.postValue(_commentCount.value!! - 1) }
        newCommentData.value = ""
    }

    fun setPostDataAsync(areaName: String?, id: Long) = launchCatching {
        _postAreaName = areaName
        _postId = id
        _post.postValue(if (id == -1L) null else withContext(Dispatchers.IO) { PostRepository.getPost(areaName, id) })
    }

    fun sendNewCommentAsync() = launchCatching {
        if (newCommentData.value != null && _postId != -1L) {
            _commentAddedEvent.postValue(
                withContext(Dispatchers.IO) {
                    CommentRepository.sendComment(
                        _postAreaName,
                        _postId,
                        newCommentData.value!!
                    )
                }
            )
            newCommentData.postValue("")
        }
    }

    fun deleteCommentAsync(position: Int, comment: Comment) = launchCatching {
        if (_postId != -1L) {
            withContext(Dispatchers.IO) { CommentRepository.deleteComment(_postAreaName, _postId, comment.id) }
            _commentRemovedEvent.postValue(position)
        }
    }
}
