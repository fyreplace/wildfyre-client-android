package app.fyreplace.client.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import app.fyreplace.client.data.models.Author
import app.fyreplace.client.data.repositories.AuthorRepository
import kotlinx.coroutines.Dispatchers

class UserFragmentViewModel(application: Application) : FailureHandlingViewModel(application) {
    private val mAuthor = MutableLiveData<Author>()

    val author: LiveData<Author> = mAuthor

    fun setAuthor(a: Author) = mAuthor.postValue(a)

    fun setUserIdAsync(id: Long) = launchCatching(Dispatchers.IO) { mAuthor.postValue(AuthorRepository.getUser(id)) }
}
