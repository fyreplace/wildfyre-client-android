package app.fyreplace.client.data.sources

import app.fyreplace.client.data.DataLoadingListener
import org.koin.dsl.module

val sourcesModule = module {
    single { (listener: DataLoadingListener) -> NotificationsDataSourceFactory(listener, get()) }
    single { (listener: DataLoadingListener) -> ArchiveDataSourceFactory(listener, get()) }
    single { (listener: DataLoadingListener) -> OwnPostsDataSourceFactory(listener, get()) }
    single { (listener: DataLoadingListener) -> DraftsDataSourceFactory(listener, get()) }
}
