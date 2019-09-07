package app.fyreplace.client.ui

import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import androidx.appcompat.app.AppCompatActivity
import app.fyreplace.client.FyreplaceApplication
import app.fyreplace.client.R
import app.fyreplace.client.data.models.ImageData
import java.io.ByteArrayOutputStream

interface ImageSelector {
    val contextWrapper: ContextWrapper

    fun startActivityForResult(intent: Intent?, requestCode: Int)

    fun onImage(image: ImageData)

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != AppCompatActivity.RESULT_OK || data == null) {
            return
        }

        fun useBytes(bytes: ByteArray, mimeType: String, extension: String) =
            onImage(ImageData("image.$extension", mimeType, bytes))

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
                    it?.run {
                        useBytes(
                            readBytes(),
                            mimeType,
                            MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)!!
                        )
                    }
                }
            }
            REQUEST_IMAGE_PHOTO -> {
                val bitmap = data.extras?.get("data") as? Bitmap
                val extension = "png"
                val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
                val buffer = ByteArrayOutputStream()
                bitmap?.compress(Bitmap.CompressFormat.PNG, 100, buffer)
                useBytes(buffer.toByteArray(), mimeType!!, extension)
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

    companion object {
        val REQUEST_IMAGE_FILE = FyreplaceApplication.context.resources
            .getInteger(R.integer.request_image_file)
        val REQUEST_IMAGE_PHOTO = FyreplaceApplication.context.resources
            .getInteger(R.integer.request_image_photo)
    }
}
