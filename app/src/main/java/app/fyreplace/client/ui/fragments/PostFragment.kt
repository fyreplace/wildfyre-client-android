package app.fyreplace.client.ui.fragments

import android.animation.LayoutTransition
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.fyreplace.client.Constants
import app.fyreplace.client.FyreplaceApplication
import app.fyreplace.client.R
import app.fyreplace.client.data.models.Comment
import app.fyreplace.client.databinding.FragmentPostBinding
import app.fyreplace.client.ui.adapters.CommentsAdapter
import app.fyreplace.client.ui.drawables.BottomSheetArrowDrawableWrapper
import app.fyreplace.client.ui.hideSoftKeyboard
import app.fyreplace.client.ui.lazyMarkdown
import app.fyreplace.client.viewmodels.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.fragment_post.*
import kotlinx.android.synthetic.main.post_comments.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.noties.markwon.recycler.MarkwonAdapter

open class PostFragment : SharingFragment(R.layout.fragment_post) {
    override val viewModels: List<FailureHandlingViewModel> by lazy { listOf(viewModel) }
    override val viewModel by lazyViewModel<PostFragmentViewModel>()
    override var menuShareContent = ""
    override val menuShareTitle by lazy { getString(R.string.post_share_title) }
    private val mainViewModel by lazyActivityViewModel<MainActivityViewModel>()
    private val fragmentArgs by navArgs<PostFragmentArgs>()
    private val onBackPressedCallback = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() = toggleComments()
    }
    private val markdown by lazyMarkdown()
    private val highlightedCommentIds by lazy {
        if (arguments != null)
            fragmentArgs.newCommentsIds?.asList()
                ?: (if (fragmentArgs.selectedCommentId >= 0) listOf(fragmentArgs.selectedCommentId) else null)
        else
            null
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        FragmentPostBinding.inflate(inflater, container, false).run {
            lifecycleOwner = viewLifecycleOwner
            model = viewModel
            return@run root
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val markdownAdapter = MarkwonAdapter.createTextViewIsRoot(R.layout.post_entry)
        content.adapter = markdownAdapter
        val commentsAdapter = CommentsAdapter(this, markdown)
        comments_list.setHasFixedSize(true)
        comments_list.adapter = commentsAdapter
        comments_list.addItemDecoration(
            DividerItemDecoration(
                view.context,
                (comments_list.layoutManager as LinearLayoutManager).orientation
            )
        )

        if (arguments != null) {
            viewModel.setPostDataAsync(fragmentArgs.areaName, fragmentArgs.postId)
        }

        viewModel.post.observe(viewLifecycleOwner, Observer {
            it?.run { menuShareContent = Constants.Api.postShareUrl(viewModel.postAreaName, viewModel.postId) }
            mainViewModel.setPost(it)
        })

        mainViewModel.userId.observe(viewLifecycleOwner, Observer { commentsAdapter.selfId = it })
        viewModel.authorId.observe(viewLifecycleOwner, Observer { commentsAdapter.authorId = it })
        viewModel.markdownContent.observe(viewLifecycleOwner, Observer {
            launch(Dispatchers.Default) {
                markdownAdapter.setMarkdown(markdown, it)
                withContext(Dispatchers.Main) { markdownAdapter.notifyDataSetChanged() }
            }
        })
        viewModel.comments.observe(viewLifecycleOwner, Observer { commentList ->
            launch(Dispatchers.Default) {
                commentsAdapter.setComments(commentList, highlightedCommentIds)
                withContext(Dispatchers.Main) { commentsAdapter.notifyDataSetChanged() }
            }
        })
        viewModel.newCommentData.observe(
            viewLifecycleOwner,
            Observer { comment_new.isEndIconVisible = it.isNotBlank() }
        )

        comments_list.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING && comment_new.hasFocus()) {
                    clearCommentInput()
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy != 0) {
                    go_up?.isVisible = recyclerView.canScrollVertically(-1) && dy < 0
                    go_down?.isVisible = recyclerView.canScrollVertically(1) && dy > 0
                }
            }
        })

        listOf(go_up.parent as ViewGroup, go_down.parent as ViewGroup).forEach {
            it.layoutTransition.setAnimator(
                LayoutTransition.APPEARING,
                ANIMATOR_APPEARING
            )
            it.layoutTransition.setAnimator(
                LayoutTransition.DISAPPEARING,
                ANIMATOR_DISAPPEARING
            )
        }

        go_up.setOnClickListener { comments_list.smoothScrollToPosition(0) }
        go_down.setOnClickListener { comments_list.smoothScrollToPosition(Math.max(commentsAdapter.itemCount - 1, 0)) }
        comment_new.setEndIconOnClickListener { clearCommentInput(); viewModel.sendNewCommentAsync() }

        collapsible_comments?.let {
            comment_count.setOnClickListener { toggleComments() }

            val commentsExpanded = savedInstanceState?.getBoolean(SAVE_COMMENTS_EXPANDED)
                ?: (highlightedCommentIds != null) || onBackPressedCallback.isEnabled
            val arrowWrapper = BottomSheetArrowDrawableWrapper(arrow, !commentsExpanded)

            BottomSheetBehavior.from(it).setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                @SuppressLint("SwitchIntDef")
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    onBackPressedCallback.isEnabled = newState == BottomSheetBehavior.STATE_EXPANDED
                    content?.isVisible = newState != BottomSheetBehavior.STATE_EXPANDED

                    when (newState) {
                        BottomSheetBehavior.STATE_COLLAPSED -> {
                            clearCommentInput()
                            arrowWrapper.setPointingUp(true)
                        }
                        BottomSheetBehavior.STATE_EXPANDED -> arrowWrapper.setPointingUp(false)
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) = Unit
            })

            if (commentsExpanded) {
                onBackPressedCallback.isEnabled = true
                toggleComments()
            }
        }
    }

    override fun onDestroyView() {
        clearCommentInput()
        super.onDestroyView()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        collapsible_comments?.let {
            outState.putBoolean(
                SAVE_COMMENTS_EXPANDED,
                BottomSheetBehavior.from(it).state == BottomSheetBehavior.STATE_EXPANDED
            )
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_post_actions, menu)
        viewModel.subscribed.observe(viewLifecycleOwner, Observer {
            menu.findItem(R.id.action_subscribe).run {
                setTitle(
                    if (it)
                        R.string.post_actions_unsubscribe
                    else
                        R.string.post_actions_subscribe
                )
                setIcon(
                    if (it)
                        R.drawable.ic_notifications_white_24dp
                    else
                        R.drawable.ic_notifications_none_white_24dp
                )
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_subscribe) {
            viewModel.changeSubscriptionAsync()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        requireActivity().menuInflater.inflate(R.menu.fragment_post_comment_context, menu)

        val position = v.tag as Int
        val comment = (comments_list.adapter as CommentsAdapter).getComment(position)

        menu.findItem(R.id.action_copy).setOnMenuItemClickListener { copyComment(comment); true }
        menu.findItem(R.id.action_share).setOnMenuItemClickListener { shareComment(comment); true }
        menu.findItem(R.id.action_delete).run {
            isVisible = comment.author?.user == mainViewModel.userId.value
            setOnMenuItemClickListener { deleteComment(position, comment); true }
        }
    }

    private fun toggleComments() {
        if (collapsible_comments == null) {
            return
        }

        val commentsBehavior = BottomSheetBehavior.from(collapsible_comments)

        when {
            commentsBehavior.state in setOf(BottomSheetBehavior.STATE_HIDDEN, BottomSheetBehavior.STATE_COLLAPSED) ->
                commentsBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            commentsBehavior.state == BottomSheetBehavior.STATE_EXPANDED ->
                commentsBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    private fun clearCommentInput() = comment_new?.let {
        hideSoftKeyboard(it)
        it.clearFocus()
    }

    private fun copyComment(comment: Comment) = getSystemService(
        requireContext(),
        ClipboardManager::class.java
    )?.setPrimaryClip(ClipData.newPlainText(getString(R.string.post_comment_copy_label), comment.text))

    private fun shareComment(comment: Comment) = shareText(
        Constants.Api.postShareUrl(viewModel.postAreaName, viewModel.postId, comment.id),
        getString(R.string.post_comment_share_title)
    )

    private fun deleteComment(position: Int, comment: Comment) {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.post_comment_delete_dialog_title)
            .setNegativeButton(R.string.no, null)
            .setPositiveButton(R.string.yes) { _, _ -> viewModel.deleteCommentAsync(position, comment) }
            .show()
    }

    private companion object {
        const val SAVE_COMMENTS_EXPANDED = "save.comments.expanded"
        val TRANSLATION_BUTTON =
            FyreplaceApplication.context.resources.getDimension(R.dimen.comments_button_translation)
        val ANIMATOR_DISAPPEARING: ObjectAnimator = ObjectAnimator.ofPropertyValuesHolder(
            Unit,
            PropertyValuesHolder.ofFloat(View.TRANSLATION_X, 0f, TRANSLATION_BUTTON),
            PropertyValuesHolder.ofFloat(View.ALPHA, 1f, 0f)
        )
        val ANIMATOR_APPEARING: ObjectAnimator = ObjectAnimator.ofPropertyValuesHolder(
            Unit,
            PropertyValuesHolder.ofFloat(View.TRANSLATION_X, TRANSLATION_BUTTON, 0f),
            PropertyValuesHolder.ofFloat(View.ALPHA, 0f, 1f)
        )
    }
}
