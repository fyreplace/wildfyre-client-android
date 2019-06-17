package app.fyreplace.client.viewmodels

import androidx.lifecycle.*
import app.fyreplace.client.data.SingleLiveEvent
import app.fyreplace.client.data.models.Comment
import app.fyreplace.client.data.models.Post
import app.fyreplace.client.data.repositories.AreaRepository
import app.fyreplace.client.data.repositories.CommentRepository
import app.fyreplace.client.data.repositories.PostRepository
import app.fyreplace.client.ui.prepareForMarkdown
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

open class PostFragmentViewModel : ViewModel() {
    var postAreaName: String = AreaRepository.preferredAreaName
        protected set
    var postId: Long = -1
        protected set
    protected val mHasContent = MutableLiveData<Boolean>()
    private val mPost = MutableLiveData<Post>()
    private val mSubscribed = MediatorLiveData<Boolean>()
    private val mMarkdownContent = MediatorLiveData<String>()
    private val mCommentAddedEvent = SingleLiveEvent<Comment>()
    private val mCommentRemovedEvent = SingleLiveEvent<Int>()
    private val mCommentCount = MediatorLiveData<Int>()

    val hasContent: LiveData<Boolean> = mHasContent
    val post: LiveData<Post?> = mPost
    val contentLoaded: LiveData<Boolean> = Transformations.map(post) { it != null }
    val authorId: LiveData<Long> = Transformations.map(post) { it?.author?.user ?: -1 }
    val comments: LiveData<List<Comment>> = Transformations.map(post) { it?.comments ?: emptyList() }
    val subscribed: LiveData<Boolean> = mSubscribed
    val markdownContent: LiveData<String> = mMarkdownContent
    val commentAddedEvent: LiveData<Comment> = mCommentAddedEvent
    val commentRemovedEvent: LiveData<Int> = mCommentRemovedEvent
    val commentCount: LiveData<Int> = mCommentCount
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

        mCommentCount.addSource(comments) { mCommentCount.postValue(it.size) }
        mCommentCount.addSource(commentAddedEvent) { mCommentCount.postValue(mCommentCount.value!! + 1) }
        mCommentCount.addSource(commentRemovedEvent) { mCommentCount.postValue(mCommentCount.value!! - 1) }
        newCommentData.value = ""
    }

    suspend fun setPostData(areaName: String?, id: Long) {
        areaName?.let {
            setPost(withContext(Dispatchers.IO) { PostRepository.getPost(it, id) })
            postAreaName = it
        }
    }

    fun setPost(post: Post?) {
        postAreaName = AreaRepository.preferredAreaName
        postId = post?.id ?: -1
        mPost.postValue(post)
    }

    suspend fun changeSubscription() = withContext(Dispatchers.IO) {
        mSubscribed.postValue(
            PostRepository.setSubscription(
                postAreaName,
                postId,
                !(subscribed.value ?: false)
            ).subscribed
        )
    }

    suspend fun sendNewComment() {
        if (newCommentData.value != null && postId != -1L) {
            mCommentAddedEvent.postValue(
                withContext(Dispatchers.IO) {
                    CommentRepository.sendComment(
                        postAreaName,
                        postId,
                        newCommentData.value!!
                    )
                }
            )
            newCommentData.postValue("")
        }
    }

    suspend fun deleteComment(position: Int, comment: Comment) {
        if (postId != -1L) {
            withContext(Dispatchers.IO) { CommentRepository.deleteComment(postAreaName, postId, comment.id) }
            mCommentRemovedEvent.postValue(position)
        }
    }
}
