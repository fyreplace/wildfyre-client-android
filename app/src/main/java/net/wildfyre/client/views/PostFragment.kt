package net.wildfyre.client.views

import android.animation.LayoutTransition
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.core.view.doOnNextLayout
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.fragment_post.*
import kotlinx.android.synthetic.main.fragment_post_comments.*
import net.wildfyre.client.R
import net.wildfyre.client.databinding.FragmentPostBinding
import net.wildfyre.client.viewmodels.FailureHandlingViewModel
import net.wildfyre.client.viewmodels.PostFragmentViewModel
import net.wildfyre.client.viewmodels.lazyViewModel
import net.wildfyre.client.views.adapters.CommentsAdapter
import net.wildfyre.client.views.drawables.BottomSheetArrowDrawableWrapper
import net.wildfyre.client.views.markdown.PostPlugin
import ru.noties.markwon.Markwon
import ru.noties.markwon.core.CorePlugin
import ru.noties.markwon.ext.strikethrough.StrikethroughPlugin
import ru.noties.markwon.image.ImagesPlugin
import ru.noties.markwon.image.okhttp.OkHttpImagesPlugin
import ru.noties.markwon.recycler.MarkwonAdapter
import ru.noties.markwon.recycler.table.TableEntryPlugin

class PostFragment : FailureHandlingFragment(R.layout.fragment_post) {
    override val viewModels: List<FailureHandlingViewModel> by lazy { listOf(viewModel) }
    private val viewModel by lazyViewModel<PostFragmentViewModel>()
    private val args by navArgs<PostFragmentArgs>()
    private val onBackPressedCallback = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() = toggleComments()
    }
    private lateinit var markdown: Markwon

    override fun onAttach(context: Context) {
        super.onAttach(context)
        setHasOptionsMenu(true)
        markdown = Markwon.builder(context)
            .usePlugin(CorePlugin.create())
            .usePlugin(StrikethroughPlugin.create())
            .usePlugin(PostPlugin.create(context))
            .usePlugin(ImagesPlugin.create(context))
            .usePlugin(OkHttpImagesPlugin.create())
            .usePlugin(TableEntryPlugin.create(context))
            .build()

        viewModel.setPostData(args.postAreaName, args.postId)
        viewModel.markdownContent.observe(this, Observer { markdownContent ->
            (content.adapter as? MarkwonAdapter)?.let {
                it.setMarkdown(markdown, markdownContent)
                it.notifyItemRangeChanged(0, it.itemCount)
            }
        })
        viewModel.comments.observe(this, Observer { commentList ->
            (comments_list.adapter as? CommentsAdapter)?.run {
                setComments(commentList, args.newCommentsIds?.asList())
                notifyItemRangeChanged(0, itemCount)
            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentPostBinding.inflate(inflater, container, false).run {
            lifecycleOwner = this@PostFragment
            model = viewModel
            return@run root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        content.adapter = MarkwonAdapter.createTextViewIsRoot(R.layout.post_entry)
        val commentsAdapter = CommentsAdapter(markdown)
        comments_list.adapter = commentsAdapter
        comments_list.addItemDecoration(
            DividerItemDecoration(
                view.context,
                (comments_list.layoutManager as LinearLayoutManager).orientation
            )
        )

        comments_list.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val upVisibility = recyclerView.canScrollVertically(-1) && dy < 0

                if (go_up.isVisible != upVisibility) {
                    go_up.isVisible = upVisibility
                }

                val downVisibility = recyclerView.canScrollVertically(1) && dy > 0

                if (go_down.isVisible != downVisibility) {
                    go_down.isVisible = downVisibility
                }
            }
        })

        listOf(go_up.parent as ViewGroup, go_down.parent as ViewGroup).forEach {
            it.layoutTransition.setAnimator(LayoutTransition.APPEARING, ANIMATOR_SCALE_UP)
            it.layoutTransition.setAnimator(LayoutTransition.DISAPPEARING, ANIMATOR_SCALE_DOWN)
        }

        go_up.setOnClickListener { comments_list.smoothScrollToPosition(0) }
        go_down.setOnClickListener { comments_list.smoothScrollToPosition(Math.max(commentsAdapter.itemCount - 1, 0)) }

        collapsible_comments?.let {
            comment_count.setOnClickListener { toggleComments() }
            it.doOnNextLayout { requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback) }

            val commentsExpanded = savedInstanceState?.getBoolean(SAVE_COMMENTS_EXPANDED)
                ?: (args.newCommentsIds != null)
            val arrowWrapper = BottomSheetArrowDrawableWrapper(arrow, !commentsExpanded)

            BottomSheetBehavior.from(it).setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                @SuppressLint("SwitchIntDef")
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    onBackPressedCallback.isEnabled = newState == BottomSheetBehavior.STATE_EXPANDED

                    when (newState) {
                        BottomSheetBehavior.STATE_COLLAPSED -> arrowWrapper.setPointingUp(true)
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        collapsible_comments?.let {
            outState.putBoolean(
                SAVE_COMMENTS_EXPANDED,
                BottomSheetBehavior.from(it).state == BottomSheetBehavior.STATE_EXPANDED
            )
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) =
        inflater.inflate(R.menu.fragment_post_actions, menu)

    private fun toggleComments() {
        if (collapsible_comments == null) {
            return
        }

        val commentsBehavior = BottomSheetBehavior.from(collapsible_comments)

        if (commentsBehavior.state in setOf(
                BottomSheetBehavior.STATE_HIDDEN,
                BottomSheetBehavior.STATE_COLLAPSED
            )
        ) {
            commentsBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        } else if (commentsBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            commentsBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    private companion object {
        const val SAVE_COMMENTS_EXPANDED = "save.comments.expanded"
        val ANIMATOR_SCALE_DOWN: ObjectAnimator = ObjectAnimator.ofPropertyValuesHolder(
            Unit,
            PropertyValuesHolder.ofFloat(View.SCALE_X, 0f),
            PropertyValuesHolder.ofFloat(View.SCALE_Y, 0f)
        )
        val ANIMATOR_SCALE_UP: ObjectAnimator = ObjectAnimator.ofPropertyValuesHolder(
            Unit,
            PropertyValuesHolder.ofFloat(View.SCALE_X, 1f),
            PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f)
        )
    }
}
