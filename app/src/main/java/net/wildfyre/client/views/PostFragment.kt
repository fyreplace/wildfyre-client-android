package net.wildfyre.client.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.annotation.DrawableRes
import androidx.core.view.doOnNextLayout
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.fragment_post.*
import net.wildfyre.client.R
import net.wildfyre.client.databinding.FragmentPostBinding
import net.wildfyre.client.viewmodels.FailureHandlingViewModel
import net.wildfyre.client.viewmodels.PostFragmentViewModel
import net.wildfyre.client.views.adapters.CommentsAdapter
import net.wildfyre.client.views.markdown.PostPlugin
import ru.noties.markwon.Markwon
import ru.noties.markwon.core.CorePlugin
import ru.noties.markwon.ext.strikethrough.StrikethroughPlugin
import ru.noties.markwon.image.ImagesPlugin
import ru.noties.markwon.image.okhttp.OkHttpImagesPlugin
import ru.noties.markwon.recycler.MarkwonAdapter
import ru.noties.markwon.recycler.table.TableEntryPlugin

class PostFragment : FailureHandlingFragment(R.layout.fragment_post) {
    private lateinit var viewModel: PostFragmentViewModel
    override val viewModels: List<FailureHandlingViewModel> by lazy { listOf(viewModel) }
    private val args by navArgs<PostFragmentArgs>()
    private val onBackPressedCallback = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() = toggleComments()
    }
    private lateinit var markdown: Markwon

    override fun onAttach(context: Context) {
        super.onAttach(context)
        setHasOptionsMenu(true)
        viewModel = ViewModelProviders.of(this).get(PostFragmentViewModel::class.java)
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
                loader.isVisible = false
                it.setMarkdown(markdown, markdownContent)
                it.notifyItemRangeChanged(0, it.itemCount)
            }
        })
        viewModel.comments.observe(this, Observer { commentList ->
            (comments_list.adapter as? CommentsAdapter)?.let {
                it.data = commentList
                it.notifyItemRangeChanged(0, it.itemCount)
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
        comments_list.adapter = CommentsAdapter(markdown)

        val layoutManager = comments_list.layoutManager as LinearLayoutManager
        comments_list.addItemDecoration(DividerItemDecoration(view.context, layoutManager.orientation))
        comment_count.setOnClickListener { toggleComments() }

        comments.doOnNextLayout { requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback) }
        BottomSheetBehavior.from(comments).setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            @SuppressLint("SwitchIntDef")
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                onBackPressedCallback.isEnabled = newState == BottomSheetBehavior.STATE_EXPANDED

                (arrow.drawable as? Animatable2Compat)?.let {
                    when (newState) {
                        BottomSheetBehavior.STATE_COLLAPSED -> animateAndSwitchTo(
                            it,
                            R.drawable.ic_arrow_drop_up_down_anim_black_24dp
                        )
                        BottomSheetBehavior.STATE_EXPANDED -> animateAndSwitchTo(
                            it,
                            R.drawable.ic_arrow_drop_down_up_anim_black_24dp
                        )
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) = Unit

            private fun animateAndSwitchTo(drawable: Animatable2Compat, @DrawableRes res: Int) {
                drawable.stop()
                drawable.start()
                drawable.registerAnimationCallback(object : Animatable2Compat.AnimationCallback() {
                    override fun onAnimationEnd(drawable: Drawable?) {
                        arrow?.setImageDrawable(AnimatedVectorDrawableCompat.create(requireContext(), res))
                    }
                })
            }
        })

        val commentsExpanded = savedInstanceState?.getBoolean(SAVE_COMMENTS_EXPANDED) == true

        if (commentsExpanded) {
            onBackPressedCallback.isEnabled = true
            toggleComments()
        }

        arrow.setImageDrawable(
            AnimatedVectorDrawableCompat.create(
                requireContext(),
                if (commentsExpanded)
                    R.drawable.ic_arrow_drop_down_up_anim_black_24dp
                else
                    R.drawable.ic_arrow_drop_up_down_anim_black_24dp
            )
        )
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(
            SAVE_COMMENTS_EXPANDED,
            BottomSheetBehavior.from(comments).state == BottomSheetBehavior.STATE_EXPANDED
        )
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) =
        inflater.inflate(R.menu.fragment_post_actions, menu)

    private fun toggleComments() {
        val commentsBehavior = BottomSheetBehavior.from(comments)

        if (commentsBehavior.state in setOf(BottomSheetBehavior.STATE_HIDDEN, BottomSheetBehavior.STATE_COLLAPSED)) {
            commentsBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        } else if (commentsBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            commentsBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    companion object {
        private const val SAVE_COMMENTS_EXPANDED = "save.comments.expanded"
    }
}
