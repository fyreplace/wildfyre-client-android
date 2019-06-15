package app.fyreplace.client.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import app.fyreplace.client.data.repositories.AuthRepository
import kotlinx.coroutines.Dispatchers

class LoginFragmentViewModel(application: Application) : FailureHandlingViewModel(application) {
    private val mAuthToken = MutableLiveData<String>()
    private val mLoginAllowed = MutableLiveData<Boolean>()

    val authToken: LiveData<String> = mAuthToken
    val loginAllowed: LiveData<Boolean> = mLoginAllowed
    val username = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val usernameValid: LiveData<Boolean> = Transformations.map(username) { it.isNotEmpty() }
    val passwordValid: LiveData<Boolean> = Transformations.map(password) { it.isNotEmpty() }

    init {
        mLoginAllowed.value = true
    }

    fun attemptLoginAsync(username: String, password: String) =
        launchCatching(Dispatchers.IO) { mAuthToken.postValue(AuthRepository.getAuthToken(username, password)) }

    fun setLoginAllowed(allowed: Boolean) = mLoginAllowed.postValue(allowed)
}
