package app.fyreplace.client.ui.widgets

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.textfield.TextInputEditText

class MarkdownEditText : TextInputEditText {
    var onSelectionChangedListener: ((Boolean) -> Unit)? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun onSelectionChanged(selStart: Int, selEnd: Int) {
        super.onSelectionChanged(selStart, selEnd)
        onSelectionChangedListener?.invoke(selStart != selEnd)
    }
}
