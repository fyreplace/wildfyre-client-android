package net.wildfyre.client.data

interface FailureHandler {
    fun onFailure(failure: Failure)
}