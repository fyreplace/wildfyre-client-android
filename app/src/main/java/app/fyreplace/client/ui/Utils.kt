package app.fyreplace.client.ui

import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import app.fyreplace.client.Constants
import app.fyreplace.client.data.models.Image
import ru.noties.markwon.Markwon
import ru.noties.markwon.core.CorePlugin
import ru.noties.markwon.ext.strikethrough.StrikethroughPlugin
import ru.noties.markwon.image.ImagesPlugin
import ru.noties.markwon.image.okhttp.OkHttpImagesPlugin

fun hideSoftKeyboard(view: View) {
    ContextCompat.getSystemService(view.context, InputMethodManager::class.java)
        ?.hideSoftInputFromWindow(view.windowToken, 0)
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

fun String.prepareForMarkdown(imageUrls: List<Image>): String = replace(Constants.Api.IMAGE_REGEX) {
    val imageNum = it.groups[1]?.value?.toInt() ?: 0
    val image = imageUrls.first { img -> img.num == imageNum }
    return@replace "\n![${image.comment}](${image.image})\n"
}
