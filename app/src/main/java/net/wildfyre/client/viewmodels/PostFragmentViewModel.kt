package net.wildfyre.client.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import net.wildfyre.client.data.Comment
import net.wildfyre.client.data.Post
import net.wildfyre.client.data.PostRepository
import net.wildfyre.client.views.markdown.prepareForMarkdown

class PostFragmentViewModel(application: Application) : FailureHandlingViewModel(application) {
    private var _postAreaName: String? = null
    private val _postId = MutableLiveData<Long>()

    val post: LiveData<Post> = Transformations.switchMap(_postId) { PostRepository.getPost(this, _postAreaName, it) }
    val contentLoaded: LiveData<Boolean> = Transformations.map(post) { it != null }
    val markdownContent: LiveData<String> = Transformations.map(post) {
        val markdownContent = StringBuilder()
        it.image?.run { markdownContent.append("![]($this)\n\n") }
        it.text?.run { markdownContent.append(prepareForMarkdown(it.additional_images)) }
        markdownContent.toString()
    }
    val comments: LiveData<List<Comment>> = Transformations.map(post) { it.comments ?: listOf() }
    val commentCount: LiveData<Int> = Transformations.map(comments) { it?.size ?: 0 }

    fun setPostData(areaName: String?, id: Long) {
        if (_postId.value != id) {
            _postAreaName = areaName
            _postId.value = id
        }
    }
}
