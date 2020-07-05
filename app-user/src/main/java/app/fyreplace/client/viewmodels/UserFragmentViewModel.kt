package app.fyreplace.client.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import app.fyreplace.client.data.models.Author
import app.fyreplace.client.data.repositories.AuthorRepository

class UserFragmentViewModel(private val authorRepository: AuthorRepository) : ViewModel() {
    private val mAuthor = MutableLiveData<Author>()

    val author: LiveData<Author> = mAuthor
    val name: LiveData<String> = mAuthor.map { it.name }
    val bio: LiveData<String> = mAuthor.map { if (it.banned) "" else it.bio }
    val banned: LiveData<Boolean> = mAuthor.map { it.banned }

    fun setAuthor(a: Author) = mAuthor.postValue(a)

    suspend fun setUserId(id: Long) = mAuthor.postValue(authorRepository.getUser(id))
}
