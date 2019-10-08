package app.fyreplace.client.ui.presenters

interface BackHandlingFragment {
    fun onGoBack(method: Method): Boolean

    enum class Method {
        BACK_BUTTON,
        UP_BUTTON,
    }
}
