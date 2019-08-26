package app.fyreplace.client.viewmodels

import androidx.lifecycle.ViewModel
import app.fyreplace.client.data.models.Post
import app.fyreplace.client.data.repositories.AreaRepository
import app.fyreplace.client.data.repositories.DraftRepository

class DraftFragmentViewModel : ViewModel() {
    private lateinit var draft: Post
    var saved = true
        private set

    suspend fun getPreferredArea() =
        AreaRepository.getAreas().firstOrNull { it.name == AreaRepository.preferredAreaName }

    fun setDraft(d: Post) {
        draft = d
    }

    fun dirtyDraft() {
        saved = false
    }

    suspend fun saveDraft(content: String) = setDraft(DraftRepository.saveDraft(draft.id, content))
        .also { saved = true }

    suspend fun deleteDraft() = DraftRepository.deleteDraft(draft.id)

    suspend fun publishDraft() = DraftRepository.publishDraft(draft.id)
}
