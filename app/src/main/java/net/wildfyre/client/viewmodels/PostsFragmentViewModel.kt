package net.wildfyre.client.viewmodels

import android.app.Application
import net.wildfyre.client.data.Post

abstract class PostsFragmentViewModel(application: Application) : ItemsListViewModel<Post>(application)
