package app.fyreplace.client.data.sources

import app.fyreplace.client.data.DataLoadingListener
import org.koin.dsl.module

val sourcesModule = module {
    factory { (listener: DataLoadingListener) -> NotificationsDataSourceFactory(listener, get()) }
    factory { (listener: DataLoadingListener) -> ArchiveDataSourceFactory(listener, get()) }
    factory { (listener: DataLoadingListener) -> OwnPostsDataSourceFactory(listener, get()) }
    factory { (listener: DataLoadingListener) -> DraftsDataSourceFactory(listener, get()) }
}
