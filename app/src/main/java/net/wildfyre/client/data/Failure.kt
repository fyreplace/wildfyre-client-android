package net.wildfyre.client.data

import androidx.annotation.StringRes

/**
 * This class holds data regarding any error that occurs when trying to fetch data remotely.
 *
 * @param error The error string resource that can be used to display an error for the user
 * @param throwable The exception that occurred
 */
data class Failure(@StringRes var error: Int, var throwable: Throwable)