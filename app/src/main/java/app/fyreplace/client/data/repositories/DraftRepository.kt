package app.fyreplace.client.data.repositories

import app.fyreplace.client.FyreplaceApplication
import app.fyreplace.client.R
import app.fyreplace.client.data.Services
import app.fyreplace.client.data.models.Draft
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object DraftRepository {
    suspend fun getDrafts(offset: Int, size: Int) = withContext(Dispatchers.IO) {
        Services.webService.getDrafts(
            AuthRepository.authToken,
            AreaRepository.preferredAreaName,
            size,
            offset
        )
    }

    suspend fun createDraft(areaName: String, anonymous: Boolean) = withContext(Dispatchers.IO) {
        Services.webService.postDraft(
            AuthRepository.authToken,
            areaName,
            Draft(FyreplaceApplication.context.getString(R.string.drafts_created_text), anonymous)
        )
    }
}
