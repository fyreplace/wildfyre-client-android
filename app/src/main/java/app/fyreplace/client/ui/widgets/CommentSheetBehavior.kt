package app.fyreplace.client.ui.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior

class CommentSheetBehavior<V : View>(context: Context?, attrs: AttributeSet?) : BottomSheetBehavior<V>(context, attrs) {
    var canDrag = true

    override fun onInterceptTouchEvent(parent: CoordinatorLayout, child: V, event: MotionEvent) =
        canDrag && super.onInterceptTouchEvent(parent, child, event)

    companion object {
        fun <V : View> from(v: V) = BottomSheetBehavior.from(v) as CommentSheetBehavior
    }
}
