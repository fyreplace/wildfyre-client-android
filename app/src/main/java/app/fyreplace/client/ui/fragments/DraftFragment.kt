package app.fyreplace.client.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import app.fyreplace.client.R
import app.fyreplace.client.data.models.ImageData
import app.fyreplace.client.ui.*
import app.fyreplace.client.viewmodels.DraftFragmentViewModel
import kotlinx.android.synthetic.main.draft_editor.*
import kotlinx.android.synthetic.main.fragment_draft.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.noties.markwon.recycler.MarkwonAdapter

class DraftFragment : FailureHandlingFragment(R.layout.fragment_draft), BackHandlingFragment,
    Toolbar.OnMenuItemClickListener, ImageSelector {
    override val viewModel by viewModel<DraftFragmentViewModel>()
    override val contextWrapper by lazy { requireActivity() }
    private val fragmentArgs by navArgs<DraftFragmentArgs>()
    private val markdown by lazyMarkdown()
    private val markdownAdapter = MarkwonAdapter.createTextViewIsRoot(R.layout.post_entry)
    private var allowDirtyingDraft = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState == null) {
            viewModel.setDraft(fragmentArgs.draft)

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

        preview?.adapter = markdownAdapter
        bottom_app_bar.setTag(R.menu.bottom_actions_fragment_draft_selection, false)
        bottom_app_bar.setOnMenuItemClickListener(this)
        editor.addTextChangedListener(EditorWatcher())
        editor.setText(fragmentArgs.draft.text)
        editor.onSelectionChangedListener = { hasSelection ->
            if (bottom_app_bar?.getTag(R.menu.bottom_actions_fragment_draft_selection) != hasSelection) {
                bottom_app_bar?.setTag(R.menu.bottom_actions_fragment_draft_selection, hasSelection)
                bottom_app_bar?.replaceMenu(
                    if (hasSelection) R.menu.bottom_actions_fragment_draft_selection
                    else R.menu.bottom_actions_fragment_draft
                )
            }
        }
    }

    override fun onDestroyView() {
        hideSoftKeyboard(editor)
        super.onDestroyView()
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        allowDirtyingDraft = true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super<FailureHandlingFragment>.onActivityResult(requestCode, resultCode, data)
        super<ImageSelector>.onActivityResult(requestCode, resultCode, data)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.actions_fragment_draft, menu)
        inflater.inflate(R.menu.actions_fragment_deletion, menu)
        menu.findItem(R.id.action_preview).isVisible = preview == null
        menu.findItem(R.id.action_delete).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_preview -> {
                hideSoftKeyboard(editor)
                AlertDialog.Builder(contextWrapper)
                    .setView(R.layout.draft_dialog_preview)
                    .show()
                    .findViewById<RecyclerView>(R.id.preview)?.adapter = markdownAdapter
                updatePreview()
            }
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

    override fun onMenuItemClick(item: MenuItem) = when (item.itemId) {
        R.id.action_title -> addTitle().let { true }
        R.id.action_list_bulleted -> addList(false).let { true }
        R.id.action_list_numbered -> addList(true).let { true }
        R.id.action_main_image -> addImage(true).let { true }
        R.id.action_images -> addImage(false).let { true }
        R.id.action_youtube -> addYoutubeLink().let { true }
        R.id.action_bold -> surroundSelectionWith("**", "**").let { true }
        R.id.action_italic -> surroundSelectionWith("_", "_").let { true }
        R.id.action_strikethrough -> surroundSelectionWith("~~", "~~").let { true }
        R.id.action_code -> surroundSelectionWith("`", "`").let { true }
        R.id.action_link -> surroundSelectionWithLink().let { true }
        else -> false
    }

    override fun onImage(image: ImageData) {
        launch {
            viewModel.addImage(image)

            if (viewModel.nextImageSlot == -1) {
                updatePreview()
            } else {
                editor?.editableText?.insert(
                    editor.selectionStart,
                    "[img: ${viewModel.nextImageSlot}]"
                )
            }
        }
    }

    private fun updatePreview() {
        markdownAdapter.setMarkdown(markdown, viewModel.draft.toMarkdown(editor.text.toString()))
        markdownAdapter.notifyDataSetChanged()
    }

    private suspend fun saveDraft(anonymous: Boolean = false, showConfirmation: Boolean = false) {
        check(!editor?.text.isNullOrBlank()) { getString(R.string.draft_action_save_empty_toast) }

        viewModel.saveDraft(editor.text.toString(), anonymous)

        if (showConfirmation) {
            Toast.makeText(context, R.string.draft_action_save_toast, Toast.LENGTH_SHORT).show()
        }
    }

    private fun addTitle() {
        AlertDialog.Builder(contextWrapper)
            .setTitle(R.string.draft_bottom_actions_title_dialog_title)
            .setItems((1..6).map { it.toString() }.toTypedArray()) { _, i ->
                editor?.editableText?.insert(editorLineStart(), "#".repeat(i + 1) + ' ')
            }
            .show()
    }

    private fun addList(numbered: Boolean) {
        editor?.editableText?.insert(editorLineStart(), if (numbered) "1. " else "- ")
    }

    private fun addImage(main: Boolean) {
        viewModel.pushImageIdentifier(main)
        var items = resources.getStringArray(R.array.draft_image_sources)

        if (main && viewModel.draft.image != null) {
            items += getString(R.string.draft_bottom_actions_images_dialog_remove)
        }

        AlertDialog.Builder(contextWrapper)
            .setTitle(
                if (main) R.string.draft_bottom_actions_main_image_dialog_title
                else R.string.draft_bottom_actions_images_dialog_title
            )
            .setItems(items) { _, i ->
                if (i == 2) {
                    launch { viewModel.removeImage() }
                } else {
                    selectImage(if (i == 0) requestImageFile else requestImagePhoto)
                }
            }
            .show()
    }

    private fun addYoutubeLink() {
        var link: EditText? = null
        link = AlertDialog.Builder(contextWrapper)
            .setTitle(R.string.draft_bottom_actions_youtube_dialog_title)
            .setView(R.layout.draft_dialog_link)
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.ok) { _, _ ->
                link?.text?.let {
                    YOUTUBE_REGEX.matchEntire(it.toString())?.run {
                        val videoId = groupValues[1]
                        val thumbnail = youtubeThumbnail(videoId)
                        editor?.editableText?.insert(
                            editor.selectionStart,
                            "[![YouTube link]($thumbnail)](https://www.youtube.com/watch?v=$videoId)"
                        )
                    } ?: Toast.makeText(
                        contextWrapper,
                        R.string.draft_bottom_actions_youtube_toast,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .show()
            .findViewById(R.id.text)
    }

    private fun editorLineStart(cursorPos: Int = editor.selectionStart) =
        editor?.editableText?.subSequence(0, cursorPos)?.indexOfLast { it == '\n' }?.plus(1) ?: -1

    private fun surroundSelectionWithLink() {
        var link: EditText? = null
        link = AlertDialog.Builder(contextWrapper)
            .setTitle(R.string.draft_bottom_actions_selection_link_dialog_title)
            .setView(R.layout.draft_dialog_link)
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.ok) { _, _ ->
                link?.text?.let { surroundSelectionWith("[", "]($it)") }
            }
            .show()
            .findViewById(R.id.text)
    }

    private fun surroundSelectionWith(start: String, end: String) {
        editor?.editableText?.insert(editor.selectionStart, start)?.insert(editor.selectionEnd, end)
    }

    private companion object {
        const val PREVIEW_DELAY = 1500L
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

            if (preview != null) {
                countDownTimer?.cancel()
                countDownTimer = Timer().apply { start() }
            }

            newText?.let {
                editor?.editableText?.append(it)
            }
        }
    }

    private inner class Timer : CountDownTimer(PREVIEW_DELAY, Long.MAX_VALUE) {
        override fun onFinish() = updatePreview()

        override fun onTick(millisUntilFinished: Long) = Unit
    }
}
