package app.fyreplace.client.ui.presenters

import androidx.appcompat.app.AppCompatActivity
import app.fyreplace.client.ui.FailureHandler
import app.fyreplace.client.ui.Presenter

abstract class FailureHandlingActivity(contentLayoutId: Int) : AppCompatActivity(contentLayoutId),
    FailureHandler, Presenter {

    override fun getContext() = this
}
