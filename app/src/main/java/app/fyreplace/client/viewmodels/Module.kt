package app.fyreplace.client.viewmodels

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelsModule = module {
    viewModel { ArchiveFragmentViewModel(get(), get()) }
    viewModel { AreaSelectingFragmentViewModel(get()) }
    viewModel { DraftFragmentViewModel(get(), get()) }
    viewModel { DraftsFragmentViewModel(get(), get(), get()) }
    viewModel { HomeFragmentViewModel(get(), get(), get()) }
    viewModel { ImageSelectorViewModel() }
    viewModel { LoginFragmentViewModel(get()) }
    viewModel { MainActivityViewModel(get(), get(), get(), get(), get()) }
    viewModel { NotificationsFragmentViewModel(get(), get()) }
    viewModel { OwnPostsFragmentViewModel(get(), get()) }
    viewModel { PostFragmentViewModel(get(), get(), get()) }
    viewModel { UserFragmentViewModel(get()) }
}
