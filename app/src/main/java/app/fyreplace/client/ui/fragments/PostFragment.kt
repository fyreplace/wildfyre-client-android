package app.fyreplace.client.ui.fragments

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.isVisible
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.fyreplace.client.Constants
import app.fyreplace.client.R
import app.fyreplace.client.data.models.Comment
import app.fyreplace.client.data.models.ImageData
import app.fyreplace.client.databinding.FragmentPostBinding
import app.fyreplace.client.ui.ImageSelector
import app.fyreplace.client.ui.adapters.CommentsAdapter
import app.fyreplace.client.ui.drawables.BottomSheetArrowDrawableWrapper
import app.fyreplace.client.ui.getShareIntent
import app.fyreplace.client.ui.hideSoftKeyboard
import app.fyreplace.client.ui.lazyMarkdown
import app.fyreplace.client.ui.widgets.CommentSheetBehavior
import app.fyreplace.client.viewmodels.MainActivityViewModel
import app.fyreplace.client.viewmodels.PostFragmentViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.fragment_post.*
import kotlinx.android.synthetic.main.post_comments.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.noties.markwon.recycler.MarkwonAdapter
import kotlin.math.max

open class PostFragment : FailureHandlingFragment(R.layout.fragment_post), BackHandlingFragment,
    ToolbarUsingFragment, ImageSelector {
    override val viewModels: List<ViewModel> by lazy { listOf(viewModel) }
    override val viewModel by viewModels<PostFragmentViewModel>()
    override val viewModelStoreOwner by lazy { this }
    override val contextWrapper by lazy { requireActivity() }
    private val mainViewModel by activityViewModels<MainActivityViewModel>()
    private val fragmentArgs by navArgs<PostFragmentArgs>()
    private val markdown by lazyMarkdown()
    private val highlightedCommentIds by lazy {
        if (canUseFragmentArgs())
            fragmentArgs.newCommentsIds?.asList()
                ?: (if (fragmentArgs.selectedCommentId >= 0) listOf(fragmentArgs.selectedCommentId) else null)
        else
            null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) =
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
        comments_list.adapter = commentsAdapter
        comments_list.addItemDecoration(
            DividerItemDecoration(
                view.context,
                (comments_list.layoutManager as LinearLayoutManager).orientation
            )
        )

        if (canUseFragmentArgs()) launch {
            fragmentArgs.post?.let { viewModel.setPost(it) }
                ?: viewModel.setPostData(fragmentArgs.areaName, fragmentArgs.postId)
            viewModel.setIsOwnPost(fragmentArgs.ownPost)
        }

        mainViewModel.userId.observe(viewLifecycleOwner) { commentsAdapter.selfId = it }
        viewModel.post.observe(viewLifecycleOwner) { mainViewModel.setPost(it) }
        viewModel.authorId.observe(viewLifecycleOwner) { commentsAdapter.authorId = it }
        viewModel.markdownContent.observe(viewLifecycleOwner) {
            lifecycleScope.launch(Dispatchers.Default) {
                markdownAdapter.setMarkdown(markdown, it)
                withContext(Dispatchers.Main) { markdownAdapter.notifyDataSetChanged() }
            }
        }
        viewModel.comments.observe(viewLifecycleOwner) {
            lifecycleScope.launch(Dispatchers.Default) {
                commentsAdapter.setComments(it, highlightedCommentIds)
                withContext(Dispatchers.Main) { commentsAdapter.notifyDataSetChanged() }
            }
        }
        viewModel.newCommentImage.observe(viewLifecycleOwner) { image ->
            if (image != null) {
                comment_new.startIconDrawable?.let {
                    DrawableCompat.setTint(
                        it,
                        ContextCompat.getColor(view.context, R.color.colorPrimary)
                    )
                }
            } else {
                comment_new.setStartIconDrawable(R.drawable.ic_attach_file_black)
            }
        }
        viewModel.canSendNewComment.observe(viewLifecycleOwner) {
            comment_new.isEndIconVisible = it
        }

        comments_list.addOnScrollListener(CommentsScrollListener())
        go_up.setOnClickListener { comments_list.smoothScrollToPosition(0) }
        go_down.setOnClickListener {
            comments_list.smoothScrollToPosition(max(commentsAdapter.itemCount - 1, 0))
        }

        for (button in listOf(go_up, go_down)) {
            button.setTag(
                R.id.anim_scale_x,
                SpringAnimation(button, SpringAnimation.SCALE_X).setSpring(BUTTON_ANIM_SPRING)
            )
            button.setTag(
                R.id.anim_scale_y,
                SpringAnimation(button, SpringAnimation.SCALE_Y)
                    .setSpring(BUTTON_ANIM_SPRING).apply {
                        addEndListener { _, _, value, _ ->
                            if (button.isVisible && value == 0f) {
                                button.isVisible = false
                            }
                        }
                    }
            )
        }

        comment_new.setEndIconOnClickListener {
            clearCommentInput()
            launch {
                viewModel.sendNewComment()
                commentsAdapter.doOnCommentsChanged { go_down.callOnClick() }
            }
        }
        comment_new.setStartIconOnClickListener { requestImage() }

        collapsible_comments?.let {
            comment_count.setOnClickListener { toggleComments() }

            val commentsExpanded = savedInstanceState?.getBoolean(SAVE_COMMENTS_EXPANDED)
                ?: (highlightedCommentIds != null)
            val arrowWrapper = BottomSheetArrowDrawableWrapper(arrow, !commentsExpanded)
            val behavior = CommentSheetBehavior.from(it)

            behavior.bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
                @SuppressLint("SwitchIntDef")
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    content?.isVisible = newState != BottomSheetBehavior.STATE_EXPANDED
                    behavior.canDrag = newState != BottomSheetBehavior.STATE_EXPANDED

                    when (newState) {
                        BottomSheetBehavior.STATE_COLLAPSED -> {
                            clearCommentInput()
                            arrowWrapper.setPointingUp(true)
                        }
                        BottomSheetBehavior.STATE_EXPANDED -> arrowWrapper.setPointingUp(false)
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) = Unit
            }

            if (commentsExpanded) {
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
        inflater.inflate(R.menu.actions_fragment_post, menu)
        inflater.inflate(R.menu.actions_fragment_sharing, menu)
        inflater.inflate(R.menu.actions_fragment_deletion, menu)

        viewModel.subscribed.observe(viewLifecycleOwner) {
            menu.findItem(R.id.action_subscribe).run {
                setTitle(
                    if (it)
                        R.string.post_action_unsubscribe
                    else
                        R.string.post_action_subscribe
                )
                setIcon(
                    if (it)
                        R.drawable.ic_notifications_white
                    else
                        R.drawable.ic_notifications_none_white
                )
            }
        }

        viewModel.post.observe(viewLifecycleOwner) {
            menu.findItem(R.id.action_share).intent = it?.let {
                getShareIntent(
                    Constants.Api.postShareUrl(viewModel.postAreaName, viewModel.postId),
                    getString(R.string.post_action_share_title)
                )
            }
        }

        val deleteItem = menu.findItem(R.id.action_delete)
        viewModel.isOwnPost.observe(viewLifecycleOwner) { deleteItem.isVisible = it }
        viewModel.authorId.observe(viewLifecycleOwner) {
            deleteItem.isVisible = it == mainViewModel.userId.value
        }

        val postMenuItems = listOf(R.id.action_subscribe, R.id.action_share, R.id.action_delete)
            .map { menu.findItem(it) }
        viewModel.contentLoaded.observe(viewLifecycleOwner) {
            postMenuItems.forEach { action -> action.isEnabled = it }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_subscribe -> launch { viewModel.changeSubscription() }
            R.id.action_delete -> AlertDialog.Builder(contextWrapper)
                .setTitle(R.string.post_action_delete_dialog_title)
                .setNegativeButton(R.string.no, null)
                .setPositiveButton(R.string.yes) { _, _ ->
                    launch {
                        viewModel.deletePost()
                        findNavController().navigateUp()
                    }
                }
                .show()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        requireActivity().menuInflater.inflate(R.menu.context_fragment_post_comment, menu)

        val position = v.tag as Int
        val comment = (comments_list.adapter as CommentsAdapter).getComment(position)

        menu.findItem(R.id.action_copy).setOnMenuItemClickListener { copyComment(comment); true }
        menu.findItem(R.id.action_share).setOnMenuItemClickListener { shareComment(comment); true }
        menu.findItem(R.id.action_delete).run {
            isVisible = comment.author?.user == mainViewModel.userId.value
            setOnMenuItemClickListener { deleteComment(position, comment); true }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super<FailureHandlingFragment>.onActivityResult(requestCode, resultCode, data)
        super<ImageSelector>.onActivityResult(requestCode, resultCode, data)
    }

    override fun onGoBack(method: BackHandlingFragment.Method) =
        method == BackHandlingFragment.Method.UP_BUTTON || collapsible_comments?.let { view ->
            (BottomSheetBehavior.from(view).state != BottomSheetBehavior.STATE_EXPANDED)
                .apply { takeUnless { it }?.run { toggleComments() } }
        } ?: true

    override fun onImage(image: ImageData) = viewModel.setCommentImage(image)

    open fun canUseFragmentArgs() = arguments != null

    private fun toggleComments() {
        collapsible_comments?.let {
            val commentsBehavior = BottomSheetBehavior.from(it)

            when {
                commentsBehavior.state in setOf(
                    BottomSheetBehavior.STATE_HIDDEN,
                    BottomSheetBehavior.STATE_COLLAPSED
                ) ->
                    commentsBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                commentsBehavior.state == BottomSheetBehavior.STATE_EXPANDED ->
                    commentsBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }
    }

    private fun clearCommentInput() = comment_new?.let {
        hideSoftKeyboard(it)
        it.clearFocus()
    }

    private fun requestImage() {
        val dialog = AlertDialog.Builder(contextWrapper)
            .setView(R.layout.post_dialog_comment_image)
            .setTitle(R.string.post_comment_attach_file_dialog_title)
            .setNegativeButton(R.string.post_comment_attach_file_dialog_negative) { _, _ ->
                selectImage(ImageSelector.REQUEST_IMAGE_PHOTO)
            }
            .setPositiveButton(R.string.post_comment_attach_file_dialog_positive) { _, _ ->
                selectImage(ImageSelector.REQUEST_IMAGE_FILE)
            }
            .setNeutralButton(R.string.post_comment_attach_file_dialog_neutral) { _, _ -> viewModel.resetCommentImage() }
            .show()

        val image = dialog.findViewById<ImageView>(R.id.image)
        image?.isVisible = viewModel.newCommentImage.value != null
        viewModel.newCommentImage.value?.let {
            image?.setImageDrawable(
                BitmapDrawable(
                    resources,
                    BitmapFactory.decodeByteArray(it.bytes, 0, it.bytes.size)
                )
            )
        }
    }

    private fun copyComment(comment: Comment) {
        getSystemService(contextWrapper, ClipboardManager::class.java)?.setPrimaryClip(
            ClipData.newPlainText(
                getString(R.string.post_comment_copy_label),
                comment.text
            )
        )

        Toast.makeText(context, getString(R.string.post_comment_copy_toast), Toast.LENGTH_SHORT)
            .show()
    }

    private fun shareComment(comment: Comment) = startActivity(
        getShareIntent(
            Constants.Api.postShareUrl(viewModel.postAreaName, viewModel.postId, comment.id),
            getString(R.string.post_comment_share_title)
        )
    )

    private fun deleteComment(position: Int, comment: Comment) {
        AlertDialog.Builder(contextWrapper)
            .setTitle(R.string.post_comment_delete_dialog_title)
            .setNegativeButton(R.string.no, null)
            .setPositiveButton(R.string.yes) { _, _ ->
                launch { viewModel.deleteComment(position, comment) }
            }
            .show()
    }

    private companion object {
        const val SAVE_COMMENTS_EXPANDED = "save.comments.expanded"
        val BUTTON_ANIM_SPRING: SpringForce = SpringForce()
            .setDampingRatio(SpringForce.DAMPING_RATIO_NO_BOUNCY)
    }

    private inner class CommentsScrollListener : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                clearCommentInput()
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            if (go_up == null || go_down == null) {
                return
            }

            for (pair in mapOf(
                go_up to (recyclerView.canScrollVertically(-1) && dy < 0),
                go_down to (recyclerView.canScrollVertically(1) && dy > 0)
            )) {
                val button = pair.key
                val visible = pair.value

                if (button.isVisible != visible) {
                    if (visible) {
                        button.isVisible = true
                    }

                    for (key in listOf(R.id.anim_scale_x, R.id.anim_scale_y)) {
                        (button.getTag(key) as? SpringAnimation)?.animateToFinalPosition(if (visible) 1f else 0f)
                    }
                }
            }
        }
    }
}
