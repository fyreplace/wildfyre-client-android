package app.fyreplace.client.ui

import android.content.Context
import android.graphics.Rect
import android.text.SpannableString
import android.text.util.Linkify
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.text.util.LinkifyCompat
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import app.fyreplace.client.R
import org.commonmark.node.SoftLineBreak
import org.commonmark.node.Text
import ru.noties.markwon.AbstractMarkwonPlugin
import ru.noties.markwon.MarkwonConfiguration
import ru.noties.markwon.MarkwonVisitor
import ru.noties.markwon.core.CorePlugin
import ru.noties.markwon.image.AsyncDrawableLoader
import ru.noties.markwon.image.ImageSize
import ru.noties.markwon.image.ImageSizeResolver
import ru.noties.markwon.priority.Priority

class PostPlugin private constructor(private val context: Context) : AbstractMarkwonPlugin() {
    override fun configureVisitor(builder: MarkwonVisitor.Builder) {
        builder.on(SoftLineBreak::class.java) { visitor, _ -> visitor.forceNewLine() }
        builder.on(Text::class.java) { visitor, text ->
            visitor.builder().append(
                SpannableString(text.literal)
                    .apply { LinkifyCompat.addLinks(this, Linkify.WEB_URLS) })
            visitor.visitChildren(text)
        }
    }

    override fun configureImages(builder: AsyncDrawableLoader.Builder) {
        builder.placeholderDrawableProvider {
            VectorDrawableCompat.create(
                context.resources,
                R.drawable.ic_image_daynight,
                context.theme
            )?.apply {
                DrawableCompat.setTint(
                    this,
                    ContextCompat.getColor(context, R.color.colorOnBackground)
                )
            }
        }
    }

    override fun configureConfiguration(builder: MarkwonConfiguration.Builder) {
        builder.imageSizeResolver(object : ImageSizeResolver() {
            override fun resolveImageSize(
                imageSize: ImageSize?,
                imageBounds: Rect,
                canvasWidth: Int,
                textSize: Float
            ): Rect {
                val factor = canvasWidth.toFloat() / imageBounds.width()
                return Rect(0, 0, canvasWidth, (imageBounds.bottom * factor).toInt())
            }
        })
    }

    override fun priority() = Priority.after(CorePlugin::class.java)

    companion object {
        fun create(context: Context) = PostPlugin(context)
    }
}
