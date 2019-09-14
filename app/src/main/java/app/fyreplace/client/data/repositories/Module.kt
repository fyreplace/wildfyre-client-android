package app.fyreplace.client.data.repositories

import org.koin.dsl.module

val repositoriesModule = module {
    single { AreaRepository(get(), get(), get()) }
    single { AuthorRepository(get(), get()) }
    single { AuthRepository(get(), get()) }
    single { CommentRepository(get(), get()) }
    single { DraftRepository(get(), get(), get(), get()) }
    single { NotificationRepository(get(), get()) }
    single { PostRepository(get(), get(), get()) }
    single { SettingsRepository(get()) }
}
