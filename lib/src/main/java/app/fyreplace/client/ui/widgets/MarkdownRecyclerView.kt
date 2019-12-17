package app.fyreplace.client.ui.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.noties.markwon.image.AsyncDrawableScheduler

class MarkdownRecyclerView : RecyclerView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun onChildAttachedToWindow(child: View) {
        super.onChildAttachedToWindow(child)

        if (child is TextView) {
            AsyncDrawableScheduler.schedule(child)
        }
    }
}
