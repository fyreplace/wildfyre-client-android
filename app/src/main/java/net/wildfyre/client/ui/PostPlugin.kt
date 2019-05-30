package net.wildfyre.client.ui

import android.content.Context
import android.graphics.Rect
import android.os.Build
import androidx.core.content.ContextCompat
import net.wildfyre.client.R
import org.commonmark.node.SoftLineBreak
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
    }

    override fun configureImages(builder: AsyncDrawableLoader.Builder) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.placeholderDrawableProvider {
                ContextCompat.getDrawable(
                    context,
                    R.drawable.ic_image_black_24dp
                )?.apply { setTint(ContextCompat.getColor(context, R.color.foreground)) }
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

    override fun priority(): Priority = Priority.after(CorePlugin::class.java)

    companion object {
        fun create(context: Context) = PostPlugin(context)
    }
}
