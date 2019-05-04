package net.wildfyre.client.data

import android.util.Log
import androidx.annotation.CallSuper
import net.wildfyre.client.Application
import net.wildfyre.client.R

interface FailureHandler {
    @CallSuper
    fun onFailure(failure: Failure) {
        Log.e(Application.context.getString(R.string.app_name), failure.throwable.message)
    }
}