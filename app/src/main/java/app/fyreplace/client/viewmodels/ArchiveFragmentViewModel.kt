package app.fyreplace.client.viewmodels

import app.fyreplace.client.data.sources.ArchiveDataSourceFactory

class ArchiveFragmentViewModel : PostsFragmentViewModel() {
    override val factory = ArchiveDataSourceFactory(this)
}
