package net.wildfyre.client.data.sources

interface DataLoadingListener {
    fun onLoadingStart()

    fun onLoadingStop()
}
