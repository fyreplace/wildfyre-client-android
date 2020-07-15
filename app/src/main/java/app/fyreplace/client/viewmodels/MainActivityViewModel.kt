package app.fyreplace.client.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.fyreplace.client.app.R
import app.fyreplace.client.data.repositories.DraftRepository
import app.fyreplace.client.data.repositories.SettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivityViewModel(
    private val settingsRepository: SettingsRepository,
    private val draftRepository: DraftRepository
) : ViewModel() {
    private var uiRefreshTickerJob: Job? = null
    private val mUiRefreshTick = MutableLiveData<Unit>()

    val uiRefreshTick: LiveData<Unit> = mUiRefreshTick
    var startupLogin = true
        private set
    val selectedThemeIndex = MutableLiveData(THEMES.indexOfFirst { it == settingsRepository.theme })
    val shouldShowNotificationBadge = MutableLiveData(settingsRepository.showBadge)

    init {
        selectedThemeIndex.observeForever { settingsRepository.theme = getTheme(it) }
        shouldShowNotificationBadge.observeForever { settingsRepository.showBadge = it }
    }

    fun login() {
        startupLogin = false
        uiRefreshTickerJob = viewModelScope.launch(Dispatchers.IO) {
            while (true) {
                mUiRefreshTick.postValue(Unit)
                delay(UI_UPDATE_MILLIS)
            }
        }
    }

    fun logout() {
        uiRefreshTickerJob?.cancel()
    }

    fun getTheme(which: Int) = THEMES.getOrElse(which) { SettingsRepository.Themes.AUTOMATIC }

    suspend fun createDraft(text: String? = null) = draftRepository.createDraft(text)

    companion object {
        private const val UI_UPDATE_MILLIS = 60_000L

        val THEMES = arrayOf(
            SettingsRepository.Themes.AUTOMATIC,
            SettingsRepository.Themes.LIGHT,
            SettingsRepository.Themes.DARK
        )

        val NAVIGATION_LINKS = mapOf(
            R.id.fyreplace_website to R.string.main_nav_fyreplace_website_link,
            R.id.fyreplace_open_source to R.string.main_nav_fyreplace_open_source_link,
            R.id.fyreplace_privacy_policy to R.string.main_nav_fyreplace_privacy_link,
            R.id.wildfyre_website to R.string.main_nav_wildfyre_website_link,
            R.id.wildfyre_open_source to R.string.main_nav_wildfyre_open_source_link,
            R.id.wildfyre_faq to R.string.main_nav_wildfyre_faq_link,
            R.id.wildfyre_terms_and_conditions to R.string.main_nav_wildfyre_terms_link,
            R.id.wildfyre_privacy_policy to R.string.main_nav_wildfyre_privacy_link
        )
    }
}
