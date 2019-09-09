package app.fyreplace.client.ui

import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import app.fyreplace.client.FyreplaceApplication
import app.fyreplace.client.R
import app.fyreplace.client.data.models.ImageData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

interface ImageSelector : FailureHandler {
    val contextWrapper: ContextWrapper

    fun startActivityForResult(intent: Intent?, requestCode: Int)

    fun onImage(image: ImageData)

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != AppCompatActivity.RESULT_OK || data == null) {
            return
        }

        when (requestCode) {
            REQUEST_IMAGE_FILE -> {
                lateinit var mimeType: String

                contextWrapper.contentResolver.query(
                    data.data!!,
                    arrayOf(MediaStore.MediaColumns.MIME_TYPE),
                    null,
                    null,
                    null
                )?.use {
                    if (it.moveToFirst()) {
                        mimeType = it.getString(
                            it.getColumnIndex(MediaStore.MediaColumns.MIME_TYPE)
                        )
                    } else {
                        return
                    }
                } ?: return

                contextWrapper.contentResolver.openInputStream(data.data!!).use {
                    it?.run { useBytes(readBytes(), mimeType) }
                }
            }
            REQUEST_IMAGE_PHOTO -> {
                val bitmap = data.extras?.get("data") as? Bitmap
                val buffer = ByteArrayOutputStream()
                bitmap?.compress(CompressFormat.PNG, 100, buffer)
                useBytes(
                    buffer.toByteArray(),
                    MimeTypeMap.getSingleton().getMimeTypeFromExtension("png")!!
                )
            }
        }
    }

    fun selectImage(request: Int) {
        startActivityForResult(
            Intent.createChooser(
                when (request) {
                    REQUEST_IMAGE_FILE -> Intent(Intent.ACTION_GET_CONTENT)
                        .apply { type = "image/*" }
                    REQUEST_IMAGE_PHOTO -> Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    else -> return
                },
                contextWrapper.getString(R.string.image_selector_chooser)
            ),
            request
        )
    }

    private fun useBytes(bytes: ByteArray, mimeType: String) {
        launch(Dispatchers.Default) {
            var compressedBytes = bytes
            var compressedMimeType = mimeType

            if (compressedBytes.size >= IMAGE_MAX_SIZE) {
                val bitmap = BitmapFactory.decodeByteArray(compressedBytes, 0, compressedBytes.size)
                val os = ByteArrayOutputStream()
                bitmap.compress(CompressFormat.JPEG, 30, os)
                compressedBytes = os.toByteArray()
                compressedMimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension("jpg")!!
            }

            withContext(Dispatchers.Main) {
                if (compressedBytes.size <= IMAGE_MAX_SIZE) {
                    val extension = MimeTypeMap.getSingleton()
                        .getExtensionFromMimeType(compressedMimeType)
                    onImage(ImageData("image.${extension}", compressedMimeType, compressedBytes))
                } else {
                    Toast.makeText(
                        contextWrapper,
                        R.string.image_selector_error_toast,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    companion object {
        private const val IMAGE_MAX_SIZE = 512 * 1024
        val REQUEST_IMAGE_FILE = FyreplaceApplication.context.resources
            .getInteger(R.integer.request_image_file)
        val REQUEST_IMAGE_PHOTO = FyreplaceApplication.context.resources
            .getInteger(R.integer.request_image_photo)
    }
}
