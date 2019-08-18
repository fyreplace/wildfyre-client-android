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
import ru.noties.markwon.movement.MovementMethodPlugin

fun hideSoftKeyboard(view: View) {
    ContextCompat.getSystemService(view.context, InputMethodManager::class.java)
        ?.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Fragment.lazyMarkdown() = lazy {
    requireContext().let {
        Markwon.builder(it)
            .usePlugin(CorePlugin.create())
            .usePlugin(MovementMethodPlugin.create())
            .usePlugin(PostPlugin.create(it))
            .usePlugin(StrikethroughPlugin.create())
            .usePlugin(ImagesPlugin.create(it))
            .usePlugin(OkHttpImagesPlugin.create())
            .build()
    }
}

fun String.prepareForMarkdown(imageUrls: List<Image>) = replace(Constants.Api.IMAGE_REGEX) {
    val imageNum = it.groups[1]?.value?.toInt() ?: 0
    val image = imageUrls.first { img -> img.num == imageNum }
    return@replace "\n![${image.comment}](${image.image})\n"
}
