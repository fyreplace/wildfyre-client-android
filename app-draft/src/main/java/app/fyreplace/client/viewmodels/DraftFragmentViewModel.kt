package app.fyreplace.client.viewmodels

import androidx.lifecycle.ViewModel
import app.fyreplace.client.data.models.ImageData
import app.fyreplace.client.data.models.Post
import app.fyreplace.client.data.repositories.AreaRepository
import app.fyreplace.client.data.repositories.DraftRepository
import app.fyreplace.client.ui.IMAGE_REGEX

class DraftFragmentViewModel(
    private val draftRepository: DraftRepository,
    private val areaRepository: AreaRepository
) : ViewModel() {
    lateinit var draft: Post
    var saved = true
        private set
    var nextImageSlotIsMain = false
    private val nextImageSlots = mutableListOf<Int>()

    suspend fun getPreferredArea() =
        areaRepository.getAreas().firstOrNull { it.name == areaRepository.preferredAreaName }

    fun dirtyDraft() {
        saved = false
    }

    suspend fun cleanUpDraft(content: String = draft.text) {
        val usedSlots = mutableSetOf<Int>()

        for (match in IMAGE_REGEX.findAll(content)) {
            usedSlots.add(match.groupValues[1].toInt())
        }

        for (slot in draft.additionalImages.map { it.num }) {
            if (slot !in usedSlots) {
                draftRepository.removeImage(draft.id, slot)
            }
        }
    }

    suspend fun saveDraft(content: String, anonymous: Boolean) {
        cleanUpDraft(content)
        draft = draftRepository.saveDraft(draft.id, content, anonymous)
        saved = true
    }

    suspend fun deleteDraft() = draftRepository.deleteDraft(draft.id)

    suspend fun publishDraft() = draftRepository.publishDraft(draft.id)

    suspend fun addImage(image: ImageData) = if (nextImageSlotIsMain) {
        draft = draftRepository.setImage(draft.id, draft.text, image)
        -1
    } else {
        val nextImageSlot = findNextImageSlot()

        try {
            nextImageSlots.add(nextImageSlot)
            val img = draftRepository.addImage(draft.id, image, nextImageSlot)
            draft.additionalImages.add(img)
            img.num
        } finally {
            nextImageSlots.remove(nextImageSlot)
        }
    }

    suspend fun removeImage() {
        draft = draftRepository.removeImage(draft.id, draft.text)
    }

    private fun findNextImageSlot() = (draft.additionalImages.map { it.num } + nextImageSlots)
        .let { images -> (0..images.size).first { it !in images } }
}
