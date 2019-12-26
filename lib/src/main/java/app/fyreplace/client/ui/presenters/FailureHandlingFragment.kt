package app.fyreplace.client.ui.presenters

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import app.fyreplace.client.ui.FailureHandler
import app.fyreplace.client.ui.Presenter
import kotlinx.coroutines.cancel

abstract class FailureHandlingFragment(contentLayoutId: Int) : Fragment(contentLayoutId),
    FailureHandler, Presenter {
    override fun onAttach(context: Context) {
        super.onAttach(context)
        setHasOptionsMenu(true)
    }

    override fun onDestroy() {
        lifecycleScope.cancel()
        super.onDestroy()
    }

    fun navigate(uri: Uri) = startActivity(Intent(Intent.ACTION_VIEW, uri))
}
