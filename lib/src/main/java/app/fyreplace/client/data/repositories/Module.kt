package app.fyreplace.client.data.repositories

import org.koin.dsl.module

val repositoriesModule = module {
    single { AreaRepository(get(), get()) }
    single { AuthorRepository(get()) }
    single { AuthRepository(get(), get()) }
    single { CommentRepository(get()) }
    single { DraftRepository(get(), get(), get()) }
    single { NotificationRepository(get()) }
    single { PostRepository(get(), get()) }
    single { SettingsRepository(get()) }
}
