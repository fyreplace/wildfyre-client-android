package net.wildfyre.client.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import net.wildfyre.client.data.models.Author
import net.wildfyre.client.data.repositories.AuthorRepository

class UserFragmentViewModel(application: Application) : FailureHandlingViewModel(application) {
    private val mAuthor = MutableLiveData<Author>()

    val author: LiveData<Author> = mAuthor

    fun setAuthor(a: Author) = mAuthor.postValue(a)

    fun setUserIdAsync(id: Long) = launchCatching(Dispatchers.IO) { mAuthor.postValue(AuthorRepository.getUser(id)) }
}
