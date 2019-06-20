package app.fyreplace.client.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import app.fyreplace.client.data.models.Author
import app.fyreplace.client.data.repositories.AuthorRepository

class UserFragmentViewModel : ViewModel() {
    private val mAuthor = MutableLiveData<Author>()

    val author: LiveData<Author> = mAuthor

    fun setAuthor(a: Author) = mAuthor.postValue(a)

    suspend fun setUserId(id: Long) = mAuthor.postValue(AuthorRepository.getUser(id))
}
