package app.fyreplace.client.viewmodels

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelsModule = module {
    viewModel { CentralViewModel(get(), get(), get()) }
    viewModel { AreaSelectorViewModel(get()) }
    viewModel { ImageSelectorViewModel() }
    viewModel { MainActivityViewModel(get(), get()) }
    viewModel { NewDraftActivityViewModel(get()) }
    viewModel { LoginFragmentViewModel(get()) }
    viewModel { HomeFragmentViewModel(get(), get(), get()) }
    viewModel { NotificationsFragmentViewModel(get(), get()) }
    viewModel { ArchiveFragmentViewModel(get(), get()) }
    viewModel { DraftsFragmentViewModel(get(), get(), get()) }
    viewModel { OwnPostsFragmentViewModel(get(), get()) }
    viewModel { ProfileFragmentViewModel() }
    viewModel { PostFragmentViewModel(get(), get(), get()) }
    viewModel { DraftFragmentViewModel(get(), get()) }
    viewModel { UserFragmentViewModel(get()) }
}
