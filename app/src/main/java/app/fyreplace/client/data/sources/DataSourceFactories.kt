package app.fyreplace.client.data.sources

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import app.fyreplace.client.data.models.Notification
import app.fyreplace.client.data.models.Post
import app.fyreplace.client.data.repositories.DraftRepository
import app.fyreplace.client.data.repositories.NotificationRepository
import app.fyreplace.client.data.repositories.PostRepository

abstract class ItemsDataSourceFactory<I> : DataSource.Factory<Int, I>() {
    private val mDataSource = MutableLiveData<DataSource<Int, I>>()

    val dataSource: LiveData<DataSource<Int, I>> = mDataSource

    override fun create(): DataSource<Int, I> = newSource().also { mDataSource.postValue(it) }

    protected abstract fun newSource(): ItemsDataSource<I>
}

class NotificationsDataSourceFactory(
    private val listener: DataLoadingListener,
    private val notificationRepository: NotificationRepository
) :
    ItemsDataSourceFactory<Notification>() {
    override fun newSource() = NotificationsDataSource(listener, notificationRepository)
}

class ArchiveDataSourceFactory(
    private val listener: DataLoadingListener,
    private val postRepository: PostRepository
) :
    ItemsDataSourceFactory<Post>() {
    override fun newSource() = ArchiveDataSource(listener, postRepository)
}

class OwnPostsDataSourceFactory(
    private val listener: DataLoadingListener,
    private val postRepository: PostRepository
) :
    ItemsDataSourceFactory<Post>() {
    override fun newSource() = OwnPostsDataSource(listener, postRepository)
}

class DraftsDataSourceFactory(
    private val listener: DataLoadingListener,
    private val postRepository: DraftRepository
) :
    ItemsDataSourceFactory<Post>() {
    override fun newSource() = DraftsDataSource(listener, postRepository)
}
