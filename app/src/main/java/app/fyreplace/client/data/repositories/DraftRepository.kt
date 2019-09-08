package app.fyreplace.client.data.repositories

import app.fyreplace.client.FyreplaceApplication
import app.fyreplace.client.R
import app.fyreplace.client.data.Services
import app.fyreplace.client.data.models.Draft
import app.fyreplace.client.data.models.DraftNoImageContent
import app.fyreplace.client.data.models.ImageData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody.Part.createFormData

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

    suspend fun saveDraft(id: Long, content: String, anonymous: Boolean) =
        withContext(Dispatchers.IO) {
        Services.webService.patchDraft(
            AuthRepository.authToken,
            AreaRepository.preferredAreaName,
            id,
            Draft(content, anonymous)
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

    suspend fun setImage(id: Long, content: String, image: ImageData) =
        withContext(Dispatchers.IO) {
            Services.webService.putImage(
                AuthRepository.authToken,
                AreaRepository.preferredAreaName,
                id,
                createFormData("image", image),
                createFormData("text", content)
            )
        }

    suspend fun addImage(id: Long, image: ImageData, slot: Int) = withContext(Dispatchers.IO) {
        Services.webService.putImage(
            AuthRepository.authToken,
            AreaRepository.preferredAreaName,
            id,
            slot,
            createFormData("image", image),
            createFormData("comment", "Image $slot")
        )
    }

    suspend fun removeImage(id: Long, content: String) = withContext(Dispatchers.IO) {
        Services.webService.putEmptyImage(
            AuthRepository.authToken,
            AreaRepository.preferredAreaName,
            id,
            DraftNoImageContent(content)
        )
    }

    suspend fun removeImage(id: Long, slot: Int = -1) = withContext(Dispatchers.IO) {
        Services.webService.deleteImage(
            AuthRepository.authToken,
            AreaRepository.preferredAreaName,
            id,
            slot
        )
        return@withContext
    }
}
