package app.fyreplace.client.ui

import android.graphics.Rect
import android.text.SpannableString
import android.text.util.Linkify
import androidx.core.text.util.LinkifyCompat
import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.MarkwonConfiguration
import io.noties.markwon.MarkwonVisitor
import io.noties.markwon.image.AsyncDrawable
import io.noties.markwon.image.ImageSizeResolver
import org.commonmark.node.Text

class PostPlugin private constructor() : AbstractMarkwonPlugin() {
    override fun configureVisitor(builder: MarkwonVisitor.Builder) {
        builder.on(Text::class.java) { visitor, text ->
            val textWithLinks = SpannableString(text.literal)
            LinkifyCompat.addLinks(textWithLinks, Linkify.WEB_URLS)
            visitor.builder().append(textWithLinks)
            visitor.visitChildren(text)
        }
    }

    override fun configureConfiguration(builder: MarkwonConfiguration.Builder) {
        builder.imageSizeResolver(object : ImageSizeResolver() {
            override fun resolveImageSize(drawable: AsyncDrawable): Rect {
                val canvasWidth = drawable.lastKnownCanvasWidth
                val factor = canvasWidth.toFloat() / drawable.intrinsicWidth
                return Rect(0, 0, canvasWidth, (drawable.intrinsicHeight * factor).toInt())
            }
        })
    }

    companion object {
        fun create() = PostPlugin()
    }
}
