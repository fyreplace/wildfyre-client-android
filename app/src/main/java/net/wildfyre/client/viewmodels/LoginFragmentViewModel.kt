package net.wildfyre.client.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import net.wildfyre.client.data.AuthRepository

class LoginFragmentViewModel(application: Application) : FailureHandlingViewModel(application) {
    private val _canLogin = MediatorLiveData<Boolean>()

    val username = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val canLogin: LiveData<Boolean>
        get() = _canLogin

    init {
        val canLoginUpdater = Observer<String> {
            val usernameStr = username.value
            val passwordStr = password.value
            _canLogin.value = usernameStr != null && usernameStr.isNotEmpty()
                    && passwordStr != null && passwordStr.isNotEmpty()
        }

        _canLogin.addSource(username, canLoginUpdater)
        _canLogin.addSource(password, canLoginUpdater)
    }

    fun attemptLogin(username: String, password: String) {
        AuthRepository.fetchAuthToken(this, username, password)
    }
}