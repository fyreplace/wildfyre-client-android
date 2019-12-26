package app.fyreplace.client.viewmodels

import androidx.lifecycle.ViewModel
import app.fyreplace.client.data.repositories.AreaRepository

class NewDraftActivityViewModel(
    private val areaRepository: AreaRepository
) : ViewModel() {
    suspend fun getAreas() = areaRepository.getAreas()

    fun setPreferredAreaName(areaName: String) {
        areaRepository.preferredAreaName = areaName
    }
}
