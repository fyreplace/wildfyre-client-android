package app.fyreplace.client.viewmodels

import androidx.lifecycle.*
import app.fyreplace.client.data.models.Comment
import app.fyreplace.client.data.models.ImageData
import app.fyreplace.client.data.models.Post
import app.fyreplace.client.data.repositories.AreaRepository
import app.fyreplace.client.data.repositories.CommentRepository
import app.fyreplace.client.data.repositories.PostRepository
import app.fyreplace.client.ui.prepareForMarkdown
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

open class PostFragmentViewModel : ViewModel() {
    var postAreaName = AreaRepository.preferredAreaName
        private set
    var postId = -1L
        private set
    protected val mHasContent = MutableLiveData<Boolean>()
    private val mPost = MutableLiveData<Post>()
    private val mSubscribed = MediatorLiveData<Boolean>()
    private val mMarkdownContent = MediatorLiveData<String>()
    private val mComments = MediatorLiveData<List<Comment>>()
    private val commentsData = mutableListOf<Comment>()
    private val mNewCommentImage = MutableLiveData<ImageData?>()
    private val mCanSendNewComment = MediatorLiveData<Boolean>()

    val hasContent: LiveData<Boolean> = mHasContent
    val post: LiveData<Post?> = mPost
    val contentLoaded: LiveData<Boolean> = post.map { it != null }
    val authorId: LiveData<Long> = post.map { it?.author?.user ?: -1 }
    val subscribed: LiveData<Boolean> = mSubscribed.distinctUntilChanged()
    val markdownContent: LiveData<String> = mMarkdownContent
    val comments: LiveData<List<Comment>> = mComments
    val commentCount: LiveData<Int> = comments.map { it.size }
    val newCommentData = MutableLiveData<String>()
    val newCommentImage: LiveData<ImageData?> = mNewCommentImage
    val canSendNewComment: LiveData<Boolean> = mCanSendNewComment

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
        mCanSendNewComment.addSource(post) { updateCanSendNewComment() }
        mCanSendNewComment.addSource(newCommentData) { updateCanSendNewComment() }
    }

    suspend fun setPostData(areaName: String?, id: Long) = areaName?.let {
        setPost(PostRepository.getPost(it, id))
        postAreaName = it
    }

    fun setPost(post: Post?) {
        if (post?.id != postId) {
            postAreaName = AreaRepository.preferredAreaName
            postId = post?.id ?: -1
            mPost.postValue(post)
            resetNewComment()
        }
    }

    suspend fun changeSubscription() = mSubscribed.postValue(
        PostRepository.setSubscription(
            postAreaName,
            postId,
            !(subscribed.value ?: false)
        ).subscribed
    )

    fun setCommentImage(image: ImageData) = mNewCommentImage.postValue(image)

    fun resetCommentImage() = mNewCommentImage.postValue(null)

    suspend fun sendNewComment() = newCommentData.value?.let { commentData ->
        if (postId != -1L) {
            try {
                mCanSendNewComment.postValue(false)
                commentsData.add(
                    CommentRepository.sendComment(
                        postAreaName,
                        postId,
                        commentData,
                        newCommentImage.value
                    )
                )
            } catch (t: Throwable) {
                mCanSendNewComment.postValue(commentData.isNotBlank())
                throw t
            }

            mComments.postValue(commentsData)
            mSubscribed.postValue(true)
            resetNewComment()
        }
    }

    suspend fun deleteComment(position: Int, comment: Comment) {
        if (postId != -1L) {
            CommentRepository.deleteComment(postAreaName, postId, comment.id)
            commentsData.removeAt(position)
            mComments.postValue(commentsData)
        }
    }

    private fun updateCanSendNewComment() =
        mCanSendNewComment.postValue(post.value != null && newCommentData.value?.isNotBlank() == true)

    private fun resetNewComment() {
        newCommentData.postValue("")
        resetCommentImage()
    }
}
