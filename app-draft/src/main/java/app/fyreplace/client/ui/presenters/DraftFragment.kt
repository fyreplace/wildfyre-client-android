package app.fyreplace.client.ui.presenters

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import app.fyreplace.client.app.draft.R
import app.fyreplace.client.app.draft.databinding.FragmentDraftBinding
import app.fyreplace.client.data.models.ImageData
import app.fyreplace.client.data.models.Post
import app.fyreplace.client.ui.*
import app.fyreplace.client.viewmodels.DraftFragmentViewModel
import com.google.android.material.snackbar.Snackbar
import io.noties.markwon.recycler.MarkwonAdapter
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import retrofit2.HttpException
import kotlin.coroutines.coroutineContext

class DraftFragment : Fragment(R.layout.fragment_draft), Presenter, BackHandlingFragment,
    Toolbar.OnMenuItemClickListener, ImageSelector {
    override val viewModel by viewModel<DraftFragmentViewModel>()
    override lateinit var bd: FragmentDraftBinding
    override val contextWrapper by lazy { requireActivity() }
    override val maxImageSize = 1f
    private val fragmentArgs by inject<Args> { parametersOf(this) }
    private val markdown by lazyMarkdown()
    private val markdownAdapter = MarkwonAdapter.createTextViewIsRoot(R.layout.post_entry)
    private var allowDirtyingDraft = false
    private var snackbar: Snackbar? = null
    private var snackbarCount = 0
    private var snackbarBatchCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        if (savedInstanceState?.getBoolean(SAVE_INIT, true) == false) {
            return
        }

        viewModel.draft = fragmentArgs.draft

        launch {
            viewModel.cleanUpDraft()

            when (val imageNumber = fragmentArgs.imageUris.size) {
                0 -> return@launch
                1 -> viewModel.nextImageSlotIsMain = true
                else -> {
                    snackbarBatchCount = imageNumber
                    bd.editor.editor.setText("")
                }
            }

            for (uri in fragmentArgs.imageUris) {
                useImageUri(uri)
            }
        }

        if (fragmentArgs.showHint) launch {
            Toast.makeText(
                context,
                getString(
                    R.string.draft_hint_toast,
                    viewModel.getPreferredArea()?.displayName
                ),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentDraftBinding.inflate(inflater).run {
        lifecycleOwner = viewLifecycleOwner
        bd = this
        return@run root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bd.preview?.adapter = markdownAdapter

        with(bd.editor) {
            bottomAppBar.setTag(R.menu.bottom_actions_fragment_draft_selection, false)
            bottomAppBar.setOnMenuItemClickListener(this@DraftFragment)
            val text = fragmentArgs.draft.text
            editor.addTextChangedListener(EditorWatcher())
            editor.setText(text)
            editor.setSelection(text.length)
            editor.onSelectionChangedListener = { hasSelection ->
                if (bottomAppBar.getTag(R.menu.bottom_actions_fragment_draft_selection) != hasSelection) {
                    bottomAppBar.setTag(
                        R.menu.bottom_actions_fragment_draft_selection,
                        hasSelection
                    )
                    bottomAppBar.replaceMenu(
                        if (hasSelection) R.menu.bottom_actions_fragment_draft_selection
                        else R.menu.bottom_actions_fragment_draft
                    )
                }
            }
        }
    }

    override fun onDestroyView() {
        hideSoftKeyboard(bd.editor.editor)
        super.onDestroyView()
    }

    override fun onResume() {
        super.onResume()
        launch {
            delay(resources.getInteger(android.R.integer.config_mediumAnimTime).toLong())
            showSoftKeyboard(bd.editor.editor)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(SAVE_INIT, false)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        allowDirtyingDraft = true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super<Fragment>.onActivityResult(requestCode, resultCode, data)
        super<ImageSelector>.onActivityResult(requestCode, resultCode, data)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.actions_fragment_draft, menu)
        inflater.inflate(R.menu.actions_fragment_deletion, menu)
        menu.findItem(R.id.action_publish).actionView.findViewById<View>(R.id.button)
            .setOnClickListener { menu.performIdentifierAction(R.id.action_publish, 0) }
        menu.findItem(R.id.action_preview).isVisible = bd.preview == null
        menu.findItem(R.id.action_delete).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_publish -> {
                var anon: Boolean? = null
                AlertDialog.Builder(contextWrapper)
                    .setTitle(R.string.draft_action_publish_dialog_title)
                    .setNegativeButton(R.string.draft_action_publish_dialog_negative) { _, _ ->
                        anon = true
                    }
                    .setPositiveButton(R.string.draft_action_publish_dialog_positive) { _, _ ->
                        anon = false
                    }
                    .setNeutralButton(R.string.cancel, null)
                    .show()
                    .setOnDismissListener {
                        anon?.let {
                            launch {
                                saveDraft(it)
                                viewModel.publishDraft()
                                Toast.makeText(
                                    context,
                                    R.string.draft_action_publish_toast,
                                    Toast.LENGTH_SHORT
                                ).show()
                                findNavController().navigateUp()
                            }
                        }
                    }
            }
            R.id.action_preview -> {
                hideSoftKeyboard(bd.editor.editor)
                AlertDialog.Builder(contextWrapper)
                    .setView(R.layout.draft_dialog_preview)
                    .show()
                    .findViewById<RecyclerView>(R.id.preview)?.adapter = markdownAdapter
                updatePreview()
            }
            R.id.action_save -> launch { saveDraft(showConfirmation = true) }
            R.id.action_delete -> AlertDialog.Builder(contextWrapper)
                .setTitle(R.string.draft_action_delete_dialog_title)
                .setNegativeButton(R.string.no, null)
                .setPositiveButton(R.string.yes) { _, _ ->
                    launch {
                        viewModel.deleteDraft()
                        findNavController().navigateUp()
                    }
                }
                .show()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onGoBack(method: BackHandlingFragment.Method): Boolean {
        hideSoftKeyboard(view)

        if (viewModel.saved) {
            return true
        }

        AlertDialog.Builder(contextWrapper)
            .setTitle(R.string.draft_back_dialog_title)
            .setNegativeButton(R.string.no) { _, _ -> findNavController().navigateUp() }
            .setPositiveButton(R.string.yes) { _, _ ->
                launch {
                    saveDraft(showConfirmation = true)
                    findNavController().navigateUp()
                }
            }
            .show()

        return false
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_title -> addTitle()
            R.id.action_list_bulleted -> addList(false)
            R.id.action_list_numbered -> addList(true)
            R.id.action_main_image -> addImage(true)
            R.id.action_images -> addImage(false)
            R.id.action_youtube -> addYoutubeLink()
            R.id.action_bold -> surroundSelectionWith("**", "**")
            R.id.action_italic -> surroundSelectionWith("_", "_")
            R.id.action_strikethrough -> surroundSelectionWith("~~", "~~")
            R.id.action_code -> surroundSelectionWith("`", "`")
            R.id.action_link -> surroundSelectionWithLink()
            else -> return false
        }

        return true
    }

    override suspend fun onImage(image: ImageData) {
        try {
            viewModel.cleanUpDraft(bd.editor.editor.text.toString())
            val imageSlot = viewModel.addImage(image)
            coroutineContext.ensureActive()

            if (imageSlot == -1) {
                updatePreview()
            } else {
                bd.editor.editor.editableText?.insert(
                    bd.editor.editor.selectionStart,
                    "[img: ${imageSlot}]\n"
                )
            }
        } catch (e: HttpException) {
            if (e.code() == 404) {
                Toast.makeText(
                    contextWrapper,
                    R.string.draft_failure_images,
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                throw e
            }
        }
    }

    override suspend fun onImageLoadingBegin() = updateCancellingSnackbar(true)

    override suspend fun onImageLoadingEnd() = updateCancellingSnackbar(false)

    private fun updatePreview() {
        markdownAdapter.setMarkdown(
            markdown,
            viewModel.draft.toMarkdown(bd.editor.editor.text.toString())
        )
        markdownAdapter.notifyDataSetChanged()
    }

    private suspend fun saveDraft(anonymous: Boolean = false, showConfirmation: Boolean = false) {
        check(!bd.editor.editor.text.isNullOrBlank()) { getString(R.string.draft_action_save_empty_toast) }
        viewModel.saveDraft(bd.editor.editor.text.toString(), anonymous)

        if (showConfirmation) {
            Toast.makeText(context, R.string.draft_action_save_toast, Toast.LENGTH_SHORT).show()
        }
    }

    private fun addTitle() {
        AlertDialog.Builder(contextWrapper)
            .setTitle(R.string.draft_bottom_action_title_dialog_title)
            .setItems((1..6).map { it.toString() }.toTypedArray()) { _, i ->
                bd.editor.editor.editableText?.insert(editorLineStart(), "#".repeat(i + 1) + ' ')
            }
            .show()
    }

    private fun addList(numbered: Boolean) {
        bd.editor.editor.editableText?.insert(editorLineStart(), if (numbered) "1. " else "- ")
    }

    private fun addImage(main: Boolean) {
        viewModel.nextImageSlotIsMain = main
        var items = resources.getStringArray(R.array.draft_image_sources)

        if (main && viewModel.draft.image != null) {
            items += getString(R.string.draft_bottom_action_images_dialog_remove)
        }

        AlertDialog.Builder(contextWrapper)
            .setTitle(
                if (main) R.string.draft_bottom_action_main_image_dialog_title
                else R.string.draft_bottom_action_images_dialog_title
            )
            .setItems(items) { _, i ->
                launch {
                    if (i == 2) {
                        viewModel.removeImage()
                        updatePreview()
                    } else {
                        selectImage(if (i == 0) requestImageFile else requestImagePhoto)
                    }
                }
            }
            .show()
    }

    private fun addYoutubeLink() {
        var link: EditText? = null
        link = AlertDialog.Builder(contextWrapper)
            .setTitle(R.string.draft_bottom_action_youtube_dialog_title)
            .setView(R.layout.draft_dialog_link)
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.ok) { _, _ ->
                link?.text?.let {
                    YOUTUBE_REGEX.matchEntire(it.toString())?.run {
                        val videoId = groupValues[1]
                        val thumbnail = youtubeThumbnail(videoId)
                        bd.editor.editor.editableText?.insert(
                            bd.editor.editor.selectionStart,
                            "[![YouTube link]($thumbnail)](https://www.youtube.com/watch?v=$videoId)"
                        )
                    } ?: Toast.makeText(
                        contextWrapper,
                        R.string.draft_bottom_action_youtube_toast,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .show()
            .findViewById(R.id.text)
    }

    private fun editorLineStart(cursorPos: Int = bd.editor.editor.selectionStart) =
        bd.editor.editor.editableText?.subSequence(0, cursorPos)
            ?.indexOfLast { it == '\n' }?.plus(1) ?: -1

    private fun surroundSelectionWithLink() {
        var link: EditText? = null
        link = AlertDialog.Builder(contextWrapper)
            .setTitle(R.string.draft_bottom_action_selection_link_dialog_title)
            .setView(R.layout.draft_dialog_link)
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.ok) { _, _ ->
                link?.text?.let { surroundSelectionWith("[", "]($it)") }
            }
            .show()
            .findViewById(R.id.text)
    }

    private fun surroundSelectionWith(start: String, end: String) {
        bd.editor.editor.run {
            editableText?.insert(selectionStart, start)?.insert(selectionEnd, end)
        }
    }

    private fun makeCancellingSnackbar() = Snackbar
        .make(
            bd.editor.bottomAppBar,
            R.string.draft_bottom_action_images_snackbar,
            Snackbar.LENGTH_INDEFINITE
        )
        .setAnchorView(bd.editor.bottomAppBar)
        .apply {
            animationMode = Snackbar.ANIMATION_MODE_SLIDE
            show()
        }

    private suspend fun updateCancellingSnackbar(increaseCount: Boolean) {
        if (increaseCount) {
            if (snackbarBatchCount > 0) {
                if (snackbarCount != snackbarBatchCount) {
                    snackbarCount = snackbarBatchCount
                }

                snackbarBatchCount--
            } else {
                snackbarCount++
            }

            if (snackbar == null) {
                snackbar = makeCancellingSnackbar()
            }
        } else {
            if (snackbarCount > 0) {
                snackbarCount--
            }

            if (snackbarCount == 0) {
                snackbar?.dismiss()
                snackbar = null
            }
        }

        if (!increaseCount || snackbarBatchCount > 0) {
            snackbar?.setText(
                resources.getQuantityString(
                    R.plurals.draft_bottom_action_images_snackbar_multiple,
                    snackbarCount,
                    snackbarCount
                )
            )
        }

        val context = coroutineContext
        snackbar?.setAction(R.string.cancel) {
            snackbar = null
            snackbarCount = 0
            snackbarBatchCount = 0
            context.cancel()
        }
    }

    private companion object {
        const val SAVE_INIT = "save.init"
        const val PREVIEW_DELAY = 1500L
    }

    interface Args {
        val draft: Post
        val imageUris: List<Uri>
        val showHint: Boolean
    }

    private inner class EditorWatcher : TextWatcher {
        private var countDownTimer: CountDownTimer? = null
        private var newText: CharSequence? = null

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) = Unit

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            newText = if (s.subSequence(start, start + count).toString() == "\n") {
                val endText = s.subSequence(editorLineStart(start), s.length)

                if (Regex("^-\\s.*", RegexOption.DOT_MATCHES_ALL).matches(endText)) {
                    "- "
                } else {
                    Regex("^(\\d+)\\.\\s.*", RegexOption.DOT_MATCHES_ALL).matchEntire(endText)
                        ?.groupValues?.get(1)?.toInt()?.let { "${it + 1}. " }
                }
            } else null
        }

        override fun afterTextChanged(s: Editable) {
            if (allowDirtyingDraft) {
                viewModel.dirtyDraft()
            }

            if (bd.preview != null) {
                countDownTimer?.cancel()
                countDownTimer = Timer().apply { start() }
            }

            newText?.let {
                bd.editor.editor.editableText?.append(it)
            }
        }
    }

    private inner class Timer : CountDownTimer(PREVIEW_DELAY, Long.MAX_VALUE) {
        override fun onFinish() = updatePreview()

        override fun onTick(millisUntilFinished: Long) = Unit
    }
}
