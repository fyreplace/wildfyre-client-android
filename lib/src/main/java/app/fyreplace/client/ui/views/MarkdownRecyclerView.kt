package app.fyreplace.client.ui.views

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.children
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

    init {
        addOnScrollListener(ScrollStopListener())
    }

    private inner class ScrollStopListener : OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            if (newState == SCROLL_STATE_IDLE) {
                schedule()
            }
        }

        private fun ViewGroup.schedule() {
            for (child in children) {
                if (child is TextView) {
                    AsyncDrawableScheduler.schedule(child)
                } else if (child is ViewGroup) {
                    child.schedule()
                }
            }
        }
    }
}
