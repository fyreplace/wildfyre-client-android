package app.fyreplace.client.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import app.fyreplace.client.data.models.Comment
import app.fyreplace.client.data.models.Post
import app.fyreplace.client.data.repositories.AreaRepository
import app.fyreplace.client.data.repositories.CommentRepository
import app.fyreplace.client.data.repositories.PostRepository
import app.fyreplace.client.ui.prepareForMarkdown
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

open class PostFragmentViewModel(application: Application) : FailureHandlingViewModel(application) {
    var postAreaName: String = AreaRepository.preferredAreaName
        protected set
    var postId: Long = -1
        protected set
    protected val mHasContent = MutableLiveData<Boolean>()
    private val mPost = MutableLiveData<Post>()
    private val mSubscribed = MediatorLiveData<Boolean>()
    private val mMarkdownContent = MediatorLiveData<String>()
    private val mComments = MediatorLiveData<List<Comment>>()
    private val commentsData = mutableListOf<Comment>()

    val hasContent: LiveData<Boolean> = mHasContent
    val post: LiveData<Post?> = mPost
    val contentLoaded: LiveData<Boolean> = Transformations.map(post) { it != null }
    val authorId: LiveData<Long> = Transformations.map(post) { it?.author?.user ?: -1 }
    val subscribed: LiveData<Boolean> = mSubscribed
    val markdownContent: LiveData<String> = mMarkdownContent
    val comments: LiveData<List<Comment>> = mComments
    val commentCount: LiveData<Int> = Transformations.map(comments) { it?.size ?: 0 }
    val newCommentData = MutableLiveData<String>()

    init {
        mHasContent.value = true
        mSubscribed.addSource(post) { mSubscribed.postValue(it?.subscribed ?: false) }
        mMarkdownContent.addSource(post) {
            launchCatching(Dispatchers.Default) {
                val markdownContent = StringBuilder()
                it?.image?.run { markdownContent.append("![]($this)\n\n") }
                it?.text?.run {
                    markdownContent.append(it.additionalImages
                        ?.let { images -> prepareForMarkdown(images) } ?: this)
                }
                mMarkdownContent.postValue(markdownContent.toString())
            }
        }
        mComments.addSource(post) {
            commentsData.clear()
            it?.run { commentsData.addAll(comments) }
            mComments.postValue(commentsData)
        }
        newCommentData.value = ""
    }

    fun setPostDataAsync(areaName: String?, id: Long) = launchCatching {
        areaName?.let {
            setPost(withContext(Dispatchers.IO) { PostRepository.getPost(it, id) })
            postAreaName = it
        }
    }

    fun setPost(post: Post?) {
        if (post?.id != postId) {
            postAreaName = AreaRepository.preferredAreaName
            postId = post?.id ?: -1
            mPost.postValue(post)
            newCommentData.postValue("")
        }
    }

    fun changeSubscriptionAsync() = launchCatching(Dispatchers.IO) {
        mSubscribed.postValue(
            PostRepository.setSubscription(
                postAreaName,
                postId,
                !(subscribed.value ?: false)
            ).subscribed
        )
    }

    fun sendNewCommentAsync() = launchCatching {
        if (newCommentData.value != null && postId != -1L) {
            commentsData.add(
                withContext(Dispatchers.IO) {
                    CommentRepository.sendComment(
                        postAreaName,
                        postId,
                        newCommentData.value!!
                    )
                }
            )
            mComments.postValue(commentsData)
            newCommentData.postValue("")
            mSubscribed.postValue(true)
        }
    }

    fun deleteCommentAsync(position: Int, comment: Comment) = launchCatching {
        if (postId != -1L) {
            withContext(Dispatchers.IO) { CommentRepository.deleteComment(postAreaName, postId, comment.id) }
            commentsData.removeAt(position)
            mComments.postValue(commentsData)
        }
    }
}
