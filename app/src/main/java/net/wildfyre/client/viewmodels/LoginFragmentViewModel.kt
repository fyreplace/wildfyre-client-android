package net.wildfyre.client.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import net.wildfyre.client.data.AuthRepository

class LoginFragmentViewModel(application: Application) : FailureHandlingViewModel(application) {
    private val _loginAllowed = MutableLiveData<Boolean>()

    val username = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val usernameValid: LiveData<Boolean> = Transformations.map(username) { it.isNotEmpty() }
    val passwordValid: LiveData<Boolean> = Transformations.map(password) { it.isNotEmpty() }
    val authToken: LiveData<String> = AuthRepository.authToken
    val loginAllowed: LiveData<Boolean> = _loginAllowed

    init {
        _loginAllowed.value = true
    }

    fun attemptLogin(username: String, password: String) = AuthRepository.fetchAuthToken(this, username, password)

    fun setLoginAllowed(allowed: Boolean) {
        _loginAllowed.value = allowed
    }
}
