package app.fyreplace.client.viewmodels

import androidx.lifecycle.ViewModel
import app.fyreplace.client.data.models.Post
import app.fyreplace.client.data.repositories.DraftRepository

class DraftFragmentViewModel : ViewModel() {
    var draftId: Long? = null
        private set

    fun setDraft(draft: Post) {
        draftId = draft.id
    }

    suspend fun deleteDraft() = draftId?.let { DraftRepository.deleteDraft(it) }
}
