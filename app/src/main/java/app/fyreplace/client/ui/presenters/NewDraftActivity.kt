package app.fyreplace.client.ui.presenters

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import app.fyreplace.client.app.R
import app.fyreplace.client.data.models.Area
import app.fyreplace.client.ui.FailureHandler
import app.fyreplace.client.viewmodels.NewDraftActivityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class NewDraftActivity : AppCompatActivity(), FailureHandler {
    private val viewModel by viewModel<NewDraftActivityViewModel>()

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        launch { showDialog(viewModel.getAreas()) }
    }

    override fun getContext() = this

    private fun showDialog(areas: List<Area>) = AlertDialog.Builder(this)
        .setTitle(R.string.new_draft_dialog_title)
        .setItems(areas.map { it.displayName }.toTypedArray()) { _, i ->
            viewModel.setPreferredAreaName(areas[i].name)
            startActivity(intent.also { it.setClass(this, MainActivity::class.java) })
        }
        .setOnDismissListener { finish() }
        .show()
}
