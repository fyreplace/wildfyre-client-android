package app.fyreplace.client.ui.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import app.fyreplace.client.lib.R
import io.noties.markwon.image.AsyncDrawableScheduler
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class MarkdownRecyclerView : RecyclerView, CoroutineScope {
    private lateinit var mCoroutineContext: CoroutineContext
    override val coroutineContext: CoroutineContext
        get() = mCoroutineContext

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        mCoroutineContext = SupervisorJob() + Dispatchers.Main
        launch {
            delay(REFRESH_DELAY_INITIAL)

            while (true) {
                children.map { extractText(it) }
                    .forEach { it?.post { AsyncDrawableScheduler.schedule(it) } }
                delay(REFRESH_DELAY)
            }
        }
    }

    override fun onDetachedFromWindow() = mCoroutineContext.cancel()
        .also { super.onDetachedFromWindow() }

    private fun extractText(view: View) = view as? TextView
        ?: view.findViewById(R.id.text) as? TextView

    private companion object {
        const val REFRESH_DELAY_INITIAL = 1000L
        const val REFRESH_DELAY = 5000L
    }
}
