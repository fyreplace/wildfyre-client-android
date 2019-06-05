package net.wildfyre.client.ui

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import net.wildfyre.client.Constants
import net.wildfyre.client.R
import net.wildfyre.client.WildFyreApplication.Companion.context
import net.wildfyre.client.data.Image

fun hideSoftKeyboard(view: View) {
    context.getSystemService(Context.INPUT_METHOD_SERVICE)?.let {
        (it as InputMethodManager).hideSoftInputFromWindow(view.windowToken, 0)
    }
}

fun String.prepareForMarkdown(imageUrls: List<Image>): String =
    replace(Constants.Api.IMAGE_REGEX) {
        val imageNum = it.groups[1]?.value?.toInt() ?: 0
        val image = imageUrls.first { img -> img.num == imageNum }
        return@replace "![${image.comment}](${image.image})"
    }

fun ohNo(context: Context) {
    AlertDialog.Builder(context)
        .setTitle(R.string.alert_unimplemented_title)
        .setMessage(R.string.alert_unimplemented_message)
        .setPositiveButton(android.R.string.ok, null)
        .show()
}
