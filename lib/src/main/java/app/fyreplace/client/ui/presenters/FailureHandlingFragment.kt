package app.fyreplace.client.ui.presenters

import android.content.Context
import androidx.fragment.app.Fragment
import app.fyreplace.client.ui.FailureHandler
import app.fyreplace.client.ui.Presenter

abstract class FailureHandlingFragment(contentLayoutId: Int) : Fragment(contentLayoutId),
    FailureHandler, Presenter {
    override fun onAttach(context: Context) {
        super.onAttach(context)
        setHasOptionsMenu(true)
    }
}
