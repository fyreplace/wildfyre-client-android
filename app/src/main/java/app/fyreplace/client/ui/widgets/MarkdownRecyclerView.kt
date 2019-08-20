package app.fyreplace.client.ui.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import app.fyreplace.client.R
import kotlinx.coroutines.*
import ru.noties.markwon.image.AsyncDrawableScheduler
import kotlin.coroutines.CoroutineContext

class MarkdownRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr), CoroutineScope {
    private lateinit var mCoroutineContext: CoroutineContext
    override val coroutineContext: CoroutineContext
        get() = mCoroutineContext

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        mCoroutineContext = SupervisorJob() + Dispatchers.Main
        launch {
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
        const val REFRESH_DELAY = 5000L
    }
}
