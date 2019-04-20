package net.wildfyre.client.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import net.wildfyre.client.Application
import net.wildfyre.client.Constants
import net.wildfyre.client.R

object AuthRepository {
    private val mutableAuthToken = MutableLiveData<String>()

    val authToken: LiveData<String>
        get() = mutableAuthToken

    init {
        mutableAuthToken.value = getPrefs().getString(Constants.Preferences.AUTH_TOKEN, "") ?: ""
    }

    fun clearAuthToken() {
        setAuthToken("")
    }

    fun fetchAuthToken(fh: FailureHandler, username: String, password: String) {
        val auth = Account.Auth().also { it.username = username; it.password = password }

        Services.webService.postAuth(auth).then(fh, R.string.failure_login) {
            if (it.token != null) {
                setAuthToken("token " + it.token)
            }
        }
    }

    private fun setAuthToken(token: String) {
        mutableAuthToken.value = token
        getPrefs().edit { putString(Constants.Preferences.AUTH_TOKEN, token) }
    }
}

object AccountRepository {
    private val mutableAccount = MutableLiveData<Account>()

    val account: LiveData<Account>
        get() = mutableAccount

    init {
        mutableAccount.value = Account().apply { username = Application.context.getString(android.R.string.untitled) }
    }

    fun fetchAccount(fh: FailureHandler) {
        Services.webService.getAccount(AuthRepository.authToken.value!!).then(fh, R.string.failure_generic) {
            mutableAccount.value = it
        }
    }
}

object AuthorRepository {
    private val mutableSelf = MutableLiveData<Author>()

    val self: LiveData<Author>
        get() = mutableSelf

    fun fetchSelf(fh: FailureHandler) {
        Services.webService.getSelf(AuthRepository.authToken.value!!).then(fh, R.string.failure_generic) {
            mutableSelf.value = it
        }
    }

    fun updateSelfBio(fh: FailureHandler, bio: String) {
        Services.webService.patchBio(AuthRepository.authToken.value!!, Author().apply { this.bio = bio })
            .then(fh, R.string.failure_generic) {
                mutableSelf.value = mutableSelf.value.apply { this?.bio = bio }
            }
    }
}

object AreaRepository {
    private val mutableAreas = MutableLiveData<List<Area>>()
    private val mutablePreferredAreaName = MutableLiveData<String>()

    val areas: LiveData<List<Area>>
        get() = mutableAreas
    val preferredAreaName: LiveData<String>
        get() = mutablePreferredAreaName

    init {
        getPrefs().getString(Constants.Preferences.PREFERRED_AREA, null)?.let {
            mutablePreferredAreaName.value = it
        }
    }

    fun fetchAreas(fh: FailureHandler) {
        Services.webService.getAreas(AuthRepository.authToken.value!!).then(fh, R.string.failure_generic) { areas ->
            var total = areas.size

            fun tryUpdateAreas() {
                total--

                if (total == 0) {
                    mutableAreas.value = areas
                }
            }

            for (area in areas) {
                Services.webService.getAreaRep(AuthRepository.authToken.value!!, area.name!!)
                    .then(fh, R.string.failure_generic) {
                        area.spread = it.spread
                        area.rep = it.reputation
                        tryUpdateAreas()
                    }
            }
        }
    }

    fun setPreferredAreaName(name: String) {
        getPrefs().edit { putString(Constants.Preferences.PREFERRED_AREA, name) }
        mutablePreferredAreaName.value = name
    }
}

private fun getPrefs(): SharedPreferences {
    return Application.context.getSharedPreferences(
        Application.context.getString(R.string.app_name),
        Context.MODE_PRIVATE
    )
}