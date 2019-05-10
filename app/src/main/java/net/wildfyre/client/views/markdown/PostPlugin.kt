package net.wildfyre.client.views.markdown

import android.content.Context
import android.graphics.Rect
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
        builder.placeholderDrawableProvider { ContextCompat.getDrawable(context, R.drawable.ic_image_daynight_24dp) }
    }

    override fun configureConfiguration(builder: MarkwonConfiguration.Builder) {
        builder.imageSizeResolver(object : ImageSizeResolver() {
            override fun resolveImageSize(
                imageSize: ImageSize?,
                imageBounds: Rect,
                canvasWidth: Int,
                textSize: Float
            ): Rect {
                val horizontalMargin = context.resources.getDimensionPixelOffset(R.dimen.margin_horizontal_medium)
                val verticalMargin = context.resources.getDimensionPixelOffset(R.dimen.margin_vertical_medium)
                val imageWidth = canvasWidth - horizontalMargin * 2
                val factor = imageWidth.toFloat() / imageBounds.width()
                return Rect(
                    horizontalMargin,
                    verticalMargin,
                    horizontalMargin + imageWidth,
                    (imageBounds.bottom * factor).toInt() + verticalMargin
                )
            }
        })
    }

    override fun priority(): Priority = Priority.after(CorePlugin::class.java)

    companion object {
        fun create(context: Context) = PostPlugin(context)
    }
}