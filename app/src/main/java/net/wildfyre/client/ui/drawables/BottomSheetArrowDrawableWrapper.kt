package net.wildfyre.client.ui.drawables

import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import net.wildfyre.client.R

class BottomSheetArrowDrawableWrapper(private val image: ImageView, private var pointingUp: Boolean) {
    private var drawable = generateDrawable()

    init {
        image.setImageDrawable(drawable)
    }

    fun setPointingUp(up: Boolean) {
        if (pointingUp == up) {
            return
        }

        pointingUp = up
        drawable.start()
        drawable.registerAnimationCallback(object : Animatable2Compat.AnimationCallback() {
            override fun onAnimationEnd(d: Drawable?) {
                image.setImageDrawable(generateDrawable())
            }
        })
    }

    private fun generateDrawable(): AnimatedVectorDrawableCompat {
        drawable = AnimatedVectorDrawableCompat.create(image.context, if (pointingUp) DRAWABLE_UP else DRAWABLE_DOWN)!!
        return drawable
    }

    private companion object {
        const val DRAWABLE_UP = R.drawable.ic_arrow_drop_up_down_anim_black_24dp
        const val DRAWABLE_DOWN = R.drawable.ic_arrow_drop_down_up_anim_black_24dp
    }
}
