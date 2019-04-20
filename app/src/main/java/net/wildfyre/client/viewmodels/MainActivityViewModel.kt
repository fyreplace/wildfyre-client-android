package net.wildfyre.client.viewmodels

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import net.wildfyre.client.Constants
import net.wildfyre.client.R
import net.wildfyre.client.data.AuthRepository
import net.wildfyre.client.data.AuthorRepository

class MainActivityViewModel(application: Application) : FailureHandlingViewModel(application) {
    val authToken: LiveData<String> = AuthRepository.authToken
    val userName: LiveData<String>
        get() = Transformations.map(AuthorRepository.self) { it.name }
    val userBio: LiveData<String>
        get() = Transformations.map(AuthorRepository.self) { it.bio }
    val userAvatar: LiveData<String>
        get() = Transformations.map(AuthorRepository.self) { it.avatar }
    val themes = arrayOf(
        Pair(R.string.theme_system, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM),
        Pair(R.string.theme_auto, AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY),
        Pair(R.string.theme_light, AppCompatDelegate.MODE_NIGHT_NO),
        Pair(R.string.theme_dark, AppCompatDelegate.MODE_NIGHT_YES)
    )
    val navigationLinks = mapOf(
        Pair(R.id.about_us, Constants.Links.ABOUT_US),
        Pair(R.id.open_source, Constants.Links.OPEN_SOURCE),
        Pair(R.id.faq, Constants.Links.FAQ),
        Pair(R.id.terms_and_conditions, Constants.Links.TERMS_AND_CONDITIONS),
        Pair(R.id.privacy_policy, Constants.Links.PRIVACY_POLICY)
    )

    init {
        if (AuthRepository.authToken.value!!.isNotEmpty()) {
            updateProfile()
        }
    }

    val clearAuthToken = AuthRepository::clearAuthToken

    fun updateProfile() {
        AuthorRepository.fetchSelf(this)
    }
}