package app.fyreplace.client.ui.presenters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.fyreplace.client.app.drafts.R
import app.fyreplace.client.data.models.Post
import app.fyreplace.client.ui.adapters.PostsAdapter
import app.fyreplace.client.ui.shown
import app.fyreplace.client.viewmodels.CentralViewModel
import app.fyreplace.client.viewmodels.DraftsFragmentViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

/**
 * [androidx.fragment.app.Fragment] listing the user's drafts.
 */
class DraftsFragment : PostsFragment<DraftsFragmentViewModel>(true) {
    override val viewModel by viewModel<DraftsFragmentViewModel>()
    override val itemsAdapter = PostsAdapter(false).apply { setHasStableIds(true) }
    private val centralViewModel by sharedViewModel<CentralViewModel>()
    private val navigator by inject<Navigator> { parametersOf(this) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = super.onCreateView(inflater, container, savedInstanceState).apply {
        centralViewModel.setAllowDraftCreation(false)
        val button = inflater.inflate(R.layout.action_drafts_create, bd.container)
            .findViewById<FloatingActionButton>(R.id.button)
        button.setOnClickListener { launch { onItemClicked(viewModel.createDraft()) } }
        bd.text.setText(R.string.drafts_empty)
        bd.itemsList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                button.shown = dy <= 0
            }
        })
    }

    override fun onDestroyView() {
        bd.itemsList.clearOnScrollListeners()
        centralViewModel.setAllowDraftCreation(true)
        super.onDestroyView()
    }

    override fun onItemClicked(item: Post) {
        super.onItemClicked(item)
        navigator.navigateToDraft(item)
    }

    interface Navigator {
        fun navigateToDraft(draft: Post)
    }
}
