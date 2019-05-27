package net.wildfyre.client.views

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import net.wildfyre.client.WildFyreApplication.Companion.context

fun hideSoftKeyboard(view: View) {
    context.getSystemService(Context.INPUT_METHOD_SERVICE)?.let {
        (it as InputMethodManager).hideSoftInputFromWindow(view.windowToken, 0)
    }
}
