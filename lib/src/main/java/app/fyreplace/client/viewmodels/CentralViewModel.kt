package app.fyreplace.client.viewmodels

import androidx.lifecycle.ViewModel

abstract class CentralViewModel : ViewModel() {
    abstract fun forceNotificationCount(count: Int)
}
