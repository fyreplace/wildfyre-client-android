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

    suspend fun createDraft() = withContext(Dispatchers.IO) {
        Services.webService.postDraft(
            AuthRepository.authToken,
            AreaRepository.preferredAreaName,
            Draft(FyreplaceApplication.context.getString(R.string.drafts_created_text), false)
        )
    }

    suspend fun saveDraft(id: Long, content: String) = withContext(Dispatchers.IO) {
        Services.webService.patchDraft(
            AuthRepository.authToken,
            AreaRepository.preferredAreaName,
            id,
            Draft(content, false)
        )
    }

    suspend fun deleteDraft(id: Long) = withContext(Dispatchers.IO) {
        Services.webService.deleteDraft(
            AuthRepository.authToken,
            AreaRepository.preferredAreaName,
            id
        )
        return@withContext
    }

    suspend fun publishDraft(id: Long) = withContext(Dispatchers.IO) {
        Services.webService.postDraftPublication(
            AuthRepository.authToken,
            AreaRepository.preferredAreaName,
            id
        )
        return@withContext
    }
}
