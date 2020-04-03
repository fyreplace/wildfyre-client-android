package app.fyreplace.client.ui.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
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

    override fun onViewAdded(child: View?) {
        child?.addClick()
    }

    private fun View.addClick() {
        if (this is TextView) {
            setOnClickListener {
                AsyncDrawableScheduler.schedule(this)
            }
        } else if (this is ViewGroup) {
            for (child in children) {
                child.addClick()
            }
        }
    }
}
