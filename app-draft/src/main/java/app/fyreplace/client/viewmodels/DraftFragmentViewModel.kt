package app.fyreplace.client.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
    private val mHasMainImage = MutableLiveData<Boolean>()
    private val nextImageSlots = mutableListOf<Int>()

    lateinit var draft: Post
        private set
    val hasDraft: Boolean
        get() = ::draft.isInitialized
    var saved = true
        private set
    val hasMainImage: LiveData<Boolean> = mHasMainImage
    var nextImageSlotIsMain = false

    suspend fun getPreferredArea() =
        areaRepository.getAreas().firstOrNull { it.name == areaRepository.preferredAreaName }

    fun setDraft(post: Post) {
        draft = post
        mHasMainImage.postValue(!post.image.isNullOrEmpty())
    }

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
                draft.additionalImages.removeAll { it.num == slot }
            }
        }
    }

    suspend fun saveDraft(content: String, anonymous: Boolean) {
        draft = draftRepository.saveDraft(draft.id, content, anonymous)
        saved = true
        cleanUpDraft()
    }

    suspend fun deleteDraft() = draftRepository.deleteDraft(draft.id)

    suspend fun publishDraft() = draftRepository.publishDraft(draft.id)

    suspend fun addImage(image: ImageData) = if (nextImageSlotIsMain) {
        draft = draftRepository.setImage(draft.id, draft.text, image)
        mHasMainImage.postValue(true)
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
        mHasMainImage.postValue(false)
    }

    private fun findNextImageSlot() = (draft.additionalImages.map { it.num } + nextImageSlots)
        .let { images -> (0..images.size).first { it !in images } }
}
