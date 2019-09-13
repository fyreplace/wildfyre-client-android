package app.fyreplace.client.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel

class ImageSelectorViewModel : ViewModel() {
    private val imageUris = mutableListOf<Uri>()

    fun push(uri: Uri) = imageUris.add(uri)

    fun pop() = imageUris.removeAt(imageUris.size - 1)
}
