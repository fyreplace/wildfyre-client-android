package net.wildfyre.client.data

import androidx.annotation.StringRes

data class Failure(@StringRes var error: Int, var throwable: Throwable)