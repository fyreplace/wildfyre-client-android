package app.fyreplace.client.viewmodels

import androidx.lifecycle.ViewModel
import app.fyreplace.client.Constants
import app.fyreplace.client.data.models.ImageData
import app.fyreplace.client.data.models.Post
import app.fyreplace.client.data.repositories.AreaRepository
import app.fyreplace.client.data.repositories.DraftRepository

class DraftFragmentViewModel(
    private val draftRepository: DraftRepository,
    private val areaRepository: AreaRepository
) : ViewModel() {
    lateinit var draft: Post
        private set
    var nextImageSlot = -1
        private set
    var saved = true
        private set

    suspend fun getPreferredArea() =
        areaRepository.getAreas().firstOrNull { it.name == areaRepository.preferredAreaName }

    fun setDraft(d: Post) {
        draft = d
    }

    fun dirtyDraft() {
        saved = false
    }

    suspend fun saveDraft(content: String, anonymous: Boolean) {
        val usedSlots = mutableSetOf<Int>()

        for (match in Constants.Api.IMAGE_REGEX.findAll(content)) {
            usedSlots.add(match.groupValues[1].toInt())
        }

        for (slot in draft.additionalImages.map { it.num }) {
            if (slot !in usedSlots) {
                draftRepository.removeImage(draft.id, slot)
            }
        }

        draft = draftRepository.saveDraft(draft.id, content, anonymous)
        saved = true
    }

    suspend fun deleteDraft() = draftRepository.deleteDraft(draft.id)

    suspend fun publishDraft() = draftRepository.publishDraft(draft.id)

    fun pushImageIdentifier(main: Boolean) {
        nextImageSlot = if (main) -1 else findNextImageSlot()
    }

    suspend fun addImage(image: ImageData) {
        if (nextImageSlot == -1) {
            draft = draftRepository.setImage(draft.id, draft.text.orEmpty(), image)
        } else {
            draft.additionalImages.add(
                draftRepository.addImage(
                    draft.id,
                    image,
                    nextImageSlot
                )
            )
        }
    }

    suspend fun removeImage() {
        draft = draftRepository.removeImage(draft.id, draft.text.orEmpty())
    }

    private fun findNextImageSlot() = draft.additionalImages.map { it.num }
        .let { images -> (0..images.size).first { it !in images } }
}
