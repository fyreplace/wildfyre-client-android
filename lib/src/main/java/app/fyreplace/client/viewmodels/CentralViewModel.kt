package app.fyreplace.client.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import app.fyreplace.client.data.models.Post

abstract class CentralViewModel : ViewModel() {
    abstract val userId: LiveData<Long>

    abstract fun forceNotificationCount(count: Int)

    abstract fun setPost(post: Post?)
}
