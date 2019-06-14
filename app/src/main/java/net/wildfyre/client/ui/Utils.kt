package net.wildfyre.client.ui

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import net.wildfyre.client.Constants
import net.wildfyre.client.R
import net.wildfyre.client.data.models.Image
import ru.noties.markwon.Markwon
import ru.noties.markwon.core.CorePlugin
import ru.noties.markwon.ext.strikethrough.StrikethroughPlugin
import ru.noties.markwon.image.ImagesPlugin
import ru.noties.markwon.image.okhttp.OkHttpImagesPlugin

fun hideSoftKeyboard(view: View) {
    ContextCompat.getSystemService(view.context, InputMethodManager::class.java)
        ?.hideSoftInputFromWindow(view.windowToken, 0)
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
        .setPositiveButton(R.string.ok, null)
        .show()
}

fun Fragment.lazyMarkdown() = lazy {
    val context = requireContext()
    return@lazy Markwon.builder(context)
        .usePlugin(CorePlugin.create())
        .usePlugin(StrikethroughPlugin.create())
        .usePlugin(PostPlugin.create(context))
        .usePlugin(ImagesPlugin.create(context))
        .usePlugin(OkHttpImagesPlugin.create())
        .build()
}
