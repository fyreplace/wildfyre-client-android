package app.fyreplace.client.data.sources

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import app.fyreplace.client.data.models.Notification
import app.fyreplace.client.data.models.Post

abstract class ItemsDataSourceFactory<I> : DataSource.Factory<Int, I>() {
    private val mDataSource = MutableLiveData<DataSource<Int, I>>()

    val dataSource: LiveData<DataSource<Int, I>> = mDataSource

    override fun create(): DataSource<Int, I> = newSource().also { mDataSource.postValue(it) }

    protected abstract fun newSource(): ItemsDataSource<I>
}

class NotificationsDataSourceFactory(private val listener: DataLoadingListener) :
    ItemsDataSourceFactory<Notification>() {
    override fun newSource(): ItemsDataSource<Notification> = NotificationsDataSource(listener)
}

class ArchiveDataSourceFactory(private val listener: DataLoadingListener) : ItemsDataSourceFactory<Post>() {
    override fun newSource(): ItemsDataSource<Post> = ArchiveDataSource(listener)
}

class OwnPostsDataSourceFactory(private val listener: DataLoadingListener) : ItemsDataSourceFactory<Post>() {
    override fun newSource(): ItemsDataSource<Post> = OwnPostsDataSource(listener)
}

class DraftsDataSourceFactory(private val listener: DataLoadingListener) : ItemsDataSourceFactory<Post>() {
    override fun newSource(): ItemsDataSource<Post> = DraftsDataSource(listener)
}
