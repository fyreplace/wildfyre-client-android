package app.fyreplace.client.ui

import androidx.databinding.ViewDataBinding

interface Presenter : FailureHandler {
    val bd: ViewDataBinding
}
