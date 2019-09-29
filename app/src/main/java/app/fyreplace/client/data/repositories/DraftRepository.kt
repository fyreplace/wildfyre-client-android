package app.fyreplace.client.data.repositories

import android.content.Context
import app.fyreplace.client.R
import app.fyreplace.client.data.models.Draft
import app.fyreplace.client.data.models.DraftNoImageContent
import app.fyreplace.client.data.models.ImageData
import app.fyreplace.client.data.services.WildFyreService
import app.fyreplace.client.data.services.createFormData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody.Part.createFormData

class DraftRepository(
    private val context: Context,
    private val wildFyre: WildFyreService,
    private val areas: AreaRepository
) {
    suspend fun getDrafts(offset: Int, size: Int) = withContext(Dispatchers.IO) {
        wildFyre.getDrafts(areas.preferredAreaName, size, offset)
    }

    suspend fun createDraft() = withContext(Dispatchers.IO) {
        wildFyre.postDraft(
            areas.preferredAreaName,
            Draft(context.getString(R.string.drafts_created_text), false)
        )
    }

    suspend fun saveDraft(id: Long, content: String, anonymous: Boolean) =
        withContext(Dispatchers.IO) {
            wildFyre.patchDraft(
                areas.preferredAreaName,
                id,
                Draft(content, anonymous)
            )
        }

    suspend fun deleteDraft(id: Long) = withContext(Dispatchers.IO) {
        wildFyre.deleteDraft(areas.preferredAreaName, id)
        return@withContext
    }

    suspend fun publishDraft(id: Long) = withContext(Dispatchers.IO) {
        wildFyre.postDraftPublication(areas.preferredAreaName, id)
        return@withContext
    }

    suspend fun setImage(id: Long, content: String, image: ImageData) =
        withContext(Dispatchers.IO) {
            wildFyre.putImage(
                areas.preferredAreaName,
                id,
                createFormData("image", image),
                createFormData("text", content)
            )
        }

    suspend fun addImage(id: Long, image: ImageData, slot: Int) = withContext(Dispatchers.IO) {
        wildFyre.putImage(
            areas.preferredAreaName,
            id,
            slot,
            createFormData("image", image),
            createFormData("comment", "Image $slot")
        )
    }

    suspend fun removeImage(id: Long, content: String) = withContext(Dispatchers.IO) {
        wildFyre.putEmptyImage(areas.preferredAreaName, id, DraftNoImageContent(content))
    }

    suspend fun removeImage(id: Long, slot: Int = -1) = withContext(Dispatchers.IO) {
        wildFyre.deleteImage(areas.preferredAreaName, id, slot)
        return@withContext
    }
}
