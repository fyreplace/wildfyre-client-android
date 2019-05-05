package net.wildfyre.client.viewmodels

import android.app.Application
import net.wildfyre.client.data.Post

abstract class PostsFragmentViewModel(application: Application) : FailureHandlingViewModel(application),
    ItemsListViewModel<Post>