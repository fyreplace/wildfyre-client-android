package app.fyreplace.client.ui

import android.content.Context
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import app.fyreplace.client.AppGlide
import app.fyreplace.client.lib.R
import com.bumptech.glide.request.target.Target
import io.noties.markwon.image.AsyncDrawable
import io.noties.markwon.image.glide.GlideImagesPlugin

class PostGlideStore(private val context: Context) : GlideImagesPlugin.GlideStore {
    override fun load(drawable: AsyncDrawable) = AppGlide.with(context)
        .load(drawable.destination)
        .placeholder(
            VectorDrawableCompat.create(
                context.resources,
                R.drawable.ic_image,
                context.theme
            )
        )

    override fun cancel(target: Target<*>) = AppGlide.with(context).clear(target)
}
