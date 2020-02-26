package app.fyreplace.client.ui

import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel

interface Presenter : FailureHandler {
    val viewModel: ViewModel
    val bd: ViewDataBinding
}
