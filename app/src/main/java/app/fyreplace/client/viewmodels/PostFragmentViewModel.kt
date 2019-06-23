package app.fyreplace.client.viewmodels

import androidx.lifecycle.*
import app.fyreplace.client.data.models.Comment
import app.fyreplace.client.data.models.Post
import app.fyreplace.client.data.repositories.AreaRepository
import app.fyreplace.client.data.repositories.CommentRepository
import app.fyreplace.client.data.repositories.PostRepository
import app.fyreplace.client.ui.prepareForMarkdown
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

open class PostFragmentViewModel : ViewModel() {
    var postAreaName: String = AreaRepository.preferredAreaName
        private set
    var postId: Long = -1
        private set
    protected val mHasContent = MutableLiveData<Boolean>()
    private val mPost = MutableLiveData<Post>()
    private val mSubscribed = MediatorLiveData<Boolean>()
    private val mMarkdownContent = MediatorLiveData<String>()
    private val mComments = MediatorLiveData<List<Comment>>()
    private val commentsData = mutableListOf<Comment>()

    val hasContent: LiveData<Boolean> = mHasContent
    val post: LiveData<Post?> = mPost
    val contentLoaded: LiveData<Boolean> = post.map { it != null }
    val authorId: LiveData<Long> = post.map { it?.author?.user ?: -1 }
    val subscribed: LiveData<Boolean> = mSubscribed
    val markdownContent: LiveData<String> = mMarkdownContent
    val comments: LiveData<List<Comment>> = mComments
    val commentCount: LiveData<Int> = comments.map { it.size }
    val newCommentData = MutableLiveData<String>()

    init {
        mHasContent.value = true
        mSubscribed.addSource(post) { mSubscribed.postValue(it?.subscribed ?: false) }
        mMarkdownContent.addSource(post) {
            viewModelScope.launch(Dispatchers.Default) {
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

    suspend fun setPostData(areaName: String?, id: Long) {
        areaName?.let {
            setPost(PostRepository.getPost(it, id))
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

    suspend fun changeSubscription() = mSubscribed.postValue(
        PostRepository.setSubscription(
            postAreaName,
            postId,
            !(subscribed.value ?: false)
        ).subscribed
    )

    suspend fun sendNewComment() {
        if (newCommentData.value != null && postId != -1L) {
            commentsData.add(
                CommentRepository.sendComment(
                    postAreaName,
                    postId,
                    newCommentData.value!!
                )
            )
            mComments.postValue(commentsData)
            newCommentData.postValue("")
            mSubscribed.postValue(true)
        }
    }

    suspend fun deleteComment(position: Int, comment: Comment) {
        if (postId != -1L) {
            CommentRepository.deleteComment(postAreaName, postId, comment.id)
            commentsData.removeAt(position)
            mComments.postValue(commentsData)
        }
    }
}
