package app.fyreplace.client.ui

import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider.getUriForFile
import androidx.documentfile.provider.DocumentFile
import app.fyreplace.client.data.models.ImageData
import app.fyreplace.client.lib.R
import app.fyreplace.client.viewmodels.ImageSelectorViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.getViewModel
import java.io.ByteArrayOutputStream
import java.io.File
import java.nio.file.Paths
import kotlin.math.sqrt

interface ImageSelector : FailureHandler {
    val contextWrapper: ContextWrapper
    private val viewModel
        get() = getViewModel<ImageSelectorViewModel>()
    val requestImageFile
        get() = contextWrapper.resources.getInteger(R.integer.request_image_file)
    val requestImagePhoto
        get() = contextWrapper.resources.getInteger(R.integer.request_image_photo)

    fun startActivityForResult(intent: Intent?, requestCode: Int)

    fun onImage(image: ImageData)

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != AppCompatActivity.RESULT_OK) {
            return
        }

        launch {
            when (requestCode) {
                requestImageFile -> data?.data?.let { useImageUri(it) }
                requestImagePhoto -> viewModel.pop().let {
                    useImageUri(it)
                    DocumentFile.fromSingleUri(contextWrapper, it)?.delete()
                }
            }
        }
    }

    fun selectImage(request: Int) {
        startActivityForResult(
            Intent.createChooser(
                when (request) {
                    requestImageFile -> Intent(Intent.ACTION_GET_CONTENT)
                        .apply { type = "image/*" }
                    requestImagePhoto -> {
                        imagesDirectory().mkdirs()
                        val imageFile = File(imagesDirectory(), "image.data")
                        val imageUri = getUriForFile(
                            contextWrapper,
                            contextWrapper.getString(R.string.file_provider_authority),
                            imageFile
                        )
                        Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                            putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                            putExtra(MediaStore.EXTRA_SIZE_LIMIT, IMAGE_MAX_FILE_SIZE)
                            imageUri?.let { viewModel.push(it) } ?: return
                        }
                    }
                    else -> return
                },
                contextWrapper.getString(R.string.image_selector_chooser)
            ),
            request
        )
    }

    private fun imagesDirectory() = Paths.get(contextWrapper.filesDir.path, "images").toFile()

    private suspend fun useImageUri(uri: Uri) = withContext(Dispatchers.Default) {
        contextWrapper.contentResolver.openInputStream(uri).use {
            it?.run { useBytes(readBytes(), contextWrapper.contentResolver.getType(uri)!!) }
        }
    }

    private suspend fun useBytes(bytes: ByteArray, mimeType: String) {
        var compressedBytes = bytes
        var compressedMimeType = mimeType

        if (compressedBytes.size > IMAGE_MAX_FILE_SIZE) {
            val bitmap = BitmapFactory.decodeByteArray(compressedBytes, 0, compressedBytes.size)
            val os = ByteArrayOutputStream()
            val correctSizeBitmap = downscaleBitmap(bitmap)
            correctSizeBitmap.compress(CompressFormat.JPEG, 50, os)
            compressedBytes = os.toByteArray()
            compressedMimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension("jpg")!!
        }

        withContext(Dispatchers.Main) {
            if (compressedBytes.size <= IMAGE_MAX_FILE_SIZE) {
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

    private fun downscaleBitmap(bitmap: Bitmap): Bitmap {
        val areaFactor = (bitmap.width * bitmap.height).toFloat() / IMAGE_MAX_AREA

        if (areaFactor > 1) {
            val sideFactor = sqrt(areaFactor)
            return Bitmap.createScaledBitmap(
                bitmap,
                (bitmap.width / sideFactor).toInt(),
                (bitmap.height / sideFactor).toInt(),
                true
            )
        }

        return bitmap
    }

    companion object {
        const val IMAGE_MAX_FILE_SIZE = 512 * 1024
        const val IMAGE_MAX_AREA = 1920 * 1080
    }
}
