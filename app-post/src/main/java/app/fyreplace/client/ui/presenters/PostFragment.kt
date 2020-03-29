package app.fyreplace.client.ui.presenters

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.fyreplace.client.app.post.R
import app.fyreplace.client.app.post.databinding.FragmentPostBinding
import app.fyreplace.client.app.post.databinding.PostCommentsBinding
import app.fyreplace.client.data.models.Author
import app.fyreplace.client.data.models.Comment
import app.fyreplace.client.data.models.ImageData
import app.fyreplace.client.data.models.Post
import app.fyreplace.client.ui.*
import app.fyreplace.client.ui.adapters.CommentsAdapter
import app.fyreplace.client.ui.widgets.CommentSheetBehavior
import app.fyreplace.client.viewmodels.CentralViewModel
import app.fyreplace.client.viewmodels.PostFragmentViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import io.noties.markwon.recycler.MarkwonAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import retrofit2.HttpException
import kotlin.math.max

open class PostFragment : Fragment(R.layout.fragment_post), Presenter, BackHandlingFragment,
    ToolbarUsingFragment, ImageSelector {
    override val viewModel by viewModel<PostFragmentViewModel>()
    override lateinit var bd: FragmentPostBinding
    override val maxImageSize = 0.5f
    protected lateinit var cbd: PostCommentsBinding
    private val centralViewModel by sharedViewModel<CentralViewModel>()
    private val fragmentArgs by inject<Args> { parametersOf(this) }
    private val navigator by inject<Navigator> { parametersOf(this) }
    private val markdown by lazyMarkdown()
    private val highlightedCommentIds by lazy { if (canUseFragmentArgs()) fragmentArgs.newCommentsIds else null }
    private var commentsSheetCallback: CommentsSheetCallback<View>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentPostBinding.inflate(inflater, container, false).run {
        lifecycleOwner = viewLifecycleOwner
        model = viewModel
        bd = this
        cbd = collapsibleComments ?: staticComments ?: throw IllegalStateException()
        setHasOptionsMenu(true)

        if (collapsibleComments == null) {
            bd.buttons.comments.isVisible = false
            cbd.divider.isVisible = false
        }

        return@run root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val markdownAdapter = MarkwonAdapter.createTextViewIsRoot(R.layout.post_entry)
        val commentsAdapter = CommentsAdapter(navigator, markdown)
            .apply { doOnCommentMore(this@PostFragment::showCommentActions) }

        bd.content.adapter = markdownAdapter
        bd.buttons.comments.setOnClickListener {
            launch {
                delay(resources.getInteger(android.R.integer.config_shortAnimTime).toLong())
                viewModel.setCommentsVisible(true)
            }
        }

        cbd.commentsList.setHasFixedSize(true)
        cbd.commentsList.adapter = commentsAdapter
        cbd.commentsList.addItemDecoration(
            DividerItemDecoration(
                view.context,
                (cbd.commentsList.layoutManager as LinearLayoutManager).orientation
            )
        )

        if (canUseFragmentArgs()) launch {
            fragmentArgs.post?.let { viewModel.setPost(it) }
                ?: viewModel.setPostData(fragmentArgs.areaName, fragmentArgs.postId)

            if (fragmentArgs.ownPost) {
                viewModel.setIsOwnPost()
            }
        }

        viewModel.post.observe(viewLifecycleOwner) { centralViewModel.setPost(it) }
        viewModel.contentLoaded.observe(viewLifecycleOwner) { cbd.commentNew.isEnabled = it }
        viewModel.authorId.observe(viewLifecycleOwner) { commentsAdapter.authorId = it }
        viewModel.markdownContent.observe(viewLifecycleOwner) {
            lifecycleScope.launch(Dispatchers.Default) {
                markdownAdapter.setMarkdown(markdown, it)
                withContext(Dispatchers.Main) { markdownAdapter.notifyDataSetChanged() }
            }
        }
        viewModel.commentSheetState.observe(viewLifecycleOwner) { state ->
            bd.collapsibleComments?.root?.let { BottomSheetBehavior.from(it).state = state }
        }
        viewModel.comments.observe(viewLifecycleOwner) {
            lifecycleScope.launch(Dispatchers.Default) {
                commentsAdapter.setComments(it, highlightedCommentIds)
                withContext(Dispatchers.Main) { commentsAdapter.notifyDataSetChanged() }
            }
        }
        viewModel.newCommentImage.observe(viewLifecycleOwner) { image ->
            if (image != null) {
                cbd.commentNew.startIconDrawable?.let {
                    DrawableCompat.setTint(
                        it,
                        ContextCompat.getColor(view.context, R.color.colorSecondary)
                    )
                }
            } else {
                cbd.commentNew.setStartIconDrawable(R.drawable.ic_attach_file)
            }
        }
        viewModel.canSendNewComment.observe(viewLifecycleOwner) {
            cbd.commentNew.isEndIconVisible = it
        }

        centralViewModel.selfId.observe(viewLifecycleOwner) { viewModel.selfId = it }

        cbd.commentsList.addOnScrollListener(CommentsScrollListener())
        cbd.goUp.setOnClickListener {
            cbd.commentsList.smoothScrollToPosition(0)
        }
        cbd.goUp.setOnLongClickListener {
            cbd.commentsList.scrollToPosition(0)
            true
        }
        cbd.goDown.setOnClickListener {
            cbd.commentsList.smoothScrollToPosition(max(commentsAdapter.itemCount - 1, 0))
        }
        cbd.goDown.setOnLongClickListener {
            cbd.commentsList.scrollToPosition(max(commentsAdapter.itemCount - 1, 0))
            true
        }

        cbd.commentNew.setEndIconOnClickListener {
            clearCommentInput()
            launch {
                viewModel.sendNewComment()
                commentsAdapter.doOnCommentsChanged { cbd.goDown.callOnClick() }
            }
        }
        cbd.commentNew.setStartIconOnClickListener { requestImage() }

        bd.collapsibleComments?.run {
            val behavior = CommentSheetBehavior.from(root)
            commentsSheetCallback = CommentsSheetCallback(behavior)
                .apply { behavior.addBottomSheetCallback(this) }

            if (viewModel.commentSheetState.value == null) {
                viewModel.setCommentsVisible(highlightedCommentIds != null)
            }
        }
    }

    override fun onDestroyView() {
        cbd.commentsList.clearOnScrollListeners()

        commentsSheetCallback?.let {
            CommentSheetBehavior.from(cbd.root).removeBottomSheetCallback(it)
        }

        clearCommentInput()
        super.onDestroyView()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.actions_fragment_post, menu)
        inflater.inflate(R.menu.actions_fragment_sharing, menu)
        inflater.inflate(R.menu.actions_fragment_deletion, menu)
        inflater.inflate(R.menu.actions_fragment_flagging, menu)

        viewModel.subscribed.observe(viewLifecycleOwner) {
            menu.findItem(R.id.action_subscribe).run {
                setTitle(
                    if (it) R.string.post_action_unsubscribe
                    else R.string.post_action_subscribe
                )
                setIcon(
                    if (it) R.drawable.ic_notifications
                    else R.drawable.ic_notifications_none
                )
            }
        }

        viewModel.post.observe(viewLifecycleOwner) {
            menu.findItem(R.id.action_share).intent = it?.let {
                getShareIntent(
                    postShareUrl(viewModel.postAreaName, viewModel.postId),
                    getString(R.string.post_action_share_title)
                )
            }
        }

        viewModel.isOwnPost.observe(viewLifecycleOwner) {
            menu.findItem(R.id.action_delete).isVisible = it
            menu.findItem(R.id.action_flag).isVisible = !it
        }

        val postMenuItems =
            listOf(R.id.action_subscribe, R.id.action_share, R.id.action_delete, R.id.action_flag)
                .map { menu.findItem(it) }
        viewModel.contentLoaded.observe(viewLifecycleOwner) {
            postMenuItems.forEach { action -> action.isEnabled = it }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_subscribe -> launch { viewModel.changeSubscription() }
            R.id.action_delete -> deletePost()
            R.id.action_flag -> showFlagChoices()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super<Fragment>.onActivityResult(requestCode, resultCode, data)
        super<ImageSelector>.onActivityResult(requestCode, resultCode, data)
    }

    override fun onGoBack(method: BackHandlingFragment.Method) =
        method == BackHandlingFragment.Method.UP_BUTTON || bd.collapsibleComments?.let { binding ->
            (BottomSheetBehavior.from(binding.root).state != BottomSheetBehavior.STATE_EXPANDED)
                .apply { takeUnless { it }?.run { viewModel.setCommentsVisible(false) } }
        } ?: true

    override suspend fun onImage(image: ImageData) = viewModel.setCommentImage(image)

    override suspend fun onImageRemoved() = viewModel.resetCommentImage()

    open fun canUseFragmentArgs() = arguments != null

    private fun clearCommentInput() {
        hideSoftKeyboard(cbd.commentNew)
        cbd.commentNew.clearFocus()
    }

    private fun showCommentActions(more: View, position: Int) {
        val comment = (cbd.commentsList.adapter as CommentsAdapter).getComment(position)
        val isOwnComment = comment.author?.user == viewModel.selfId
        val popup = PopupMenu(requireContext(), more)

        popup.inflate(R.menu.actions_fragment_post_comment)
        popup.inflate(R.menu.actions_fragment_deletion)
        popup.inflate(R.menu.actions_fragment_flagging)

        with(popup.menu) {
            findItem(R.id.action_copy)
                .setOnMenuItemClickListener { copyComment(comment); true }
            findItem(R.id.action_share)
                .setOnMenuItemClickListener { shareComment(comment); true }
            findItem(R.id.action_delete).run {
                isVisible = isOwnComment
                setOnMenuItemClickListener { deleteComment(position, comment); true }
            }
            findItem(R.id.action_flag).run {
                isVisible = !isOwnComment
                setOnMenuItemClickListener { showFlagChoices(comment); true }
            }
        }

        popup.show()
    }

    private fun requestImage() {
        showImageChooser(
            R.string.post_comment_attach_file_dialog_title,
            viewModel.newCommentImage.value != null
        )
    }

    private fun deletePost() {
        MaterialAlertDialogBuilder(requiredContext)
            .setTitle(R.string.post_action_delete_dialog_title)
            .setMessage(R.string.post_action_delete_dialog_message)
            .setNegativeButton(R.string.no, null)
            .setPositiveButton(R.string.yes) { _, _ ->
                launch {
                    viewModel.deletePost()
                    findNavController().navigateUp()
                }
            }
            .show()
    }

    private fun copyComment(comment: Comment) {
        getSystemService(requiredContext, ClipboardManager::class.java)?.setPrimaryClip(
            ClipData.newPlainText(getString(R.string.post_comment_copy_label), comment.text)
        )

        Toast.makeText(
            requiredContext,
            getString(R.string.post_comment_copy_toast),
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun shareComment(comment: Comment) = startActivity(
        getShareIntent(
            postShareUrl(viewModel.postAreaName, viewModel.postId, comment.id),
            getString(R.string.post_comment_share_title)
        )
    )

    private fun deleteComment(position: Int, comment: Comment) {
        MaterialAlertDialogBuilder(requiredContext)
            .setTitle(R.string.post_comment_delete_dialog_title)
            .setMessage(R.string.post_comment_delete_dialog_message)
            .setNegativeButton(R.string.no, null)
            .setPositiveButton(R.string.yes) { _, _ ->
                launch { viewModel.deleteComment(position, comment) }
            }
            .show()
    }

    private fun showFlagChoices(comment: Comment? = null) {
        launch {
            val choices = viewModel.getFlagChoices()
            var key: Long? = null
            MaterialAlertDialogBuilder(requiredContext)
                .setTitle(R.string.post_action_flag_dialog_title)
                .setSingleChoiceItems(
                    choices.map { it.value }.toTypedArray(),
                    choices.indexOfFirst { it.key == null }
                ) { _, i -> key = choices[i].key }
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.post_action_flag_dialog_positive) { _, _ ->
                    showFlagInfo(comment, key)
                }
                .show()
        }
    }

    private fun showFlagInfo(comment: Comment?, key: Long?) {
        var editText: EditText? = null
        val alert = MaterialAlertDialogBuilder(requiredContext)
            .setTitle(R.string.post_action_flag_dialog_title)
            .setView(R.layout.post_dialog_flag_info)
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.post_action_flag) { _, _ ->
                doFlag(comment, key, editText?.text.toString())
            }
            .show()
        editText = alert.findViewById(R.id.text)
    }

    private fun doFlag(comment: Comment?, key: Long?, text: String?) = launch {
        try {
            viewModel.flag(comment?.id, key, text)
            Toast.makeText(
                requiredContext,
                if (comment == null) R.string.post_action_flag_post_toast
                else R.string.post_action_flag_comment_toast,
                Toast.LENGTH_SHORT
            ).show()
        } catch (e: HttpException) {
            if (e.code() == 400) {
                Toast.makeText(
                    requiredContext,
                    R.string.post_failure_flag,
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                throw e
            }
        }
    }

    interface Args {
        val post: Post?
        val areaName: String?
        val postId: Long
        val ownPost: Boolean
        val newCommentsIds: List<Long>?
    }

    interface Navigator {
        fun navigateToUser(author: Author)
    }

    private inner class CommentsScrollListener : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                clearCommentInput()
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            for ((button, direction) in mapOf(cbd.goUp to -1, cbd.goDown to 1)) {
                button.shown = direction * dy > 0 && recyclerView.canScrollVertically(direction)
            }
        }
    }

    private inner class CommentsSheetCallback<V : View>(private val behavior: CommentSheetBehavior<V>) :
        BottomSheetBehavior.BottomSheetCallback() {
        @SuppressLint("SwitchIntDef")
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            val notExpanded = newState != BottomSheetBehavior.STATE_EXPANDED
            bd.content.isVisible = notExpanded
            behavior.canDrag = notExpanded
            viewModel.setCommentsDividerVisibility(newState)

            when (newState) {
                BottomSheetBehavior.STATE_COLLAPSED -> {
                    clearCommentInput()
                    viewModel.setCommentsVisible(false)
                }
                BottomSheetBehavior.STATE_EXPANDED -> viewModel.setCommentsVisible(true)
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) = Unit
    }
}
