package app.fyreplace.client.ui.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.updateLayoutParams
import com.google.android.material.bottomappbar.BottomAppBar

class BottomAppBarAvoidingBehavior(context: Context?, attrs: AttributeSet?) :
    CoordinatorLayout.Behavior<View>(context, attrs) {
    private var originalMargin = 0

    override fun layoutDependsOn(parent: CoordinatorLayout, child: View, dependency: View) =
        dependency is BottomAppBar

    override fun onDependentViewChanged(
        parent: CoordinatorLayout,
        child: View,
        dependency: View
    ): Boolean {
        child.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            if (originalMargin == 0) {
                originalMargin = bottomMargin
            }

            bottomMargin = originalMargin + dependency.height
        }

        return false
    }
}
