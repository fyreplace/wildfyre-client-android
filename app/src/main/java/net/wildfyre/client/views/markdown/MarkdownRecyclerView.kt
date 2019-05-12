package net.wildfyre.client.views.markdown

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.noties.markwon.image.AsyncDrawableScheduler

class MarkdownRecyclerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr), RecyclerView.OnChildAttachStateChangeListener {
    init {
        addOnChildAttachStateChangeListener(this)
    }

    override fun onChildViewAttachedToWindow(view: View) {
        if (view is TextView) {
            AsyncDrawableScheduler.schedule(view)
        }
    }

    override fun onChildViewDetachedFromWindow(view: View) {
    }
}