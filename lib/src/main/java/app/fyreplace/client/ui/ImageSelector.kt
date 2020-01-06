package app.fyreplace.client.ui

import android.app.Activity
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider.getUriForFile
import androidx.exifinterface.media.ExifInterface
import androidx.exifinterface.media.ExifInterface.*
import app.fyreplace.client.data.models.ImageData
import app.fyreplace.client.lib.R
import app.fyreplace.client.viewmodels.ImageSelectorViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.getViewModel
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.io.InputStream
import kotlin.math.sqrt

interface ImageSelector : FailureHandler {
    val contextWrapper: ContextWrapper
    val requestImageFile
        get() = contextWrapper.resources.getInteger(R.integer.request_image_file)
    val requestImagePhoto
        get() = contextWrapper.resources.getInteger(R.integer.request_image_photo)
    private val imageSelectorViewModel
        get() = getViewModel<ImageSelectorViewModel>()
    private val imagesDirectory
        get() = File(contextWrapper.filesDir, "images")
    private val photoImageFile
        get() = File(imagesDirectory, "image.data")

    fun startActivityForResult(intent: Intent?, requestCode: Int)

    suspend fun onImage(image: ImageData)

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_OK) {
            return
        }

        launch {
            when (requestCode) {
                requestImageFile -> data?.data?.let { useImageUri(it) }
                requestImagePhoto -> imageSelectorViewModel.pop().let {
                    useImageUri(it)
                    photoImageFile.delete()
                }
            }
        }
    }

    suspend fun selectImage(request: Int) = startActivityForResult(
        Intent.createChooser(
            when (request) {
                requestImageFile -> Intent(Intent.ACTION_GET_CONTENT)
                    .apply { type = "image/*" }
                requestImagePhoto -> {
                    val imageFile = photoImageFile
                    withContext(Dispatchers.IO) { imageFile.parentFile?.mkdirs() }
                    val imageUri = getUriForFile(
                        contextWrapper,
                        contextWrapper.getString(R.string.file_provider_authority),
                        imageFile
                    )
                    Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                        putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                        putExtra(MediaStore.EXTRA_SIZE_LIMIT, IMAGE_MAX_FILE_SIZE)
                        imageUri?.let { imageSelectorViewModel.push(it) } ?: return
                    }
                }
                else -> throw IllegalArgumentException()
            },
            contextWrapper.getString(R.string.image_selector_chooser)
        ),
        request
    )

    suspend fun useImageUri(uri: Uri) = withContext(Dispatchers.Default) {
        val mimeType = contextWrapper.contentResolver.getType(uri)
            ?: throw IOException(contextWrapper.getString(R.string.image_failure_unknown_type))
        val transformations = contextWrapper.contentResolver.openInputStream(uri)
            ?.use { extractTransformations(it) }
            ?: Matrix()
        contextWrapper.contentResolver.openInputStream(uri)
            ?.use { useBytes(it.readBytes(), transformations, mimeType) }
    }

    private suspend fun extractTransformations(source: InputStream) = withContext(Dispatchers.IO) {
        val transformations = Matrix()
        val exif = ExifInterface(source)

        when (exif.getAttributeInt(TAG_ORIENTATION, ORIENTATION_UNDEFINED)) {
            ORIENTATION_ROTATE_90 -> transformations.postRotate(90f)
            ORIENTATION_ROTATE_180 -> transformations.postRotate(180f)
            ORIENTATION_ROTATE_270 -> transformations.postRotate(270f)
            ORIENTATION_FLIP_HORIZONTAL -> transformations.postScale(-1f, 1f)
            ORIENTATION_FLIP_VERTICAL -> transformations.postScale(1f, -1f)
        }

        return@withContext transformations
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    private suspend fun useBytes(bytes: ByteArray, transformations: Matrix, mimeType: String) {
        var compressedBytes = bytes
        var compressedMimeType = mimeType
        val isTooBig = compressedBytes.size > IMAGE_MAX_FILE_SIZE
        val isUnknownMime = mimeType !in listOf("jpeg", "png").map { "image/$it" }
        val isRotated = !transformations.isIdentity

        if (isTooBig || isUnknownMime || isRotated) {
            val os = ByteArrayOutputStream()
            val bitmap = BitmapFactory.decodeByteArray(compressedBytes, 0, compressedBytes.size)
            val rotatedBitmap = Bitmap.createBitmap(
                bitmap,
                0,
                0,
                bitmap.width,
                bitmap.height,
                transformations,
                true
            )

            if (isTooBig) {
                downscaleBitmap(rotatedBitmap).compress(CompressFormat.JPEG, 50, os)
            } else {
                rotatedBitmap.compress(CompressFormat.JPEG, 100, os)
            }

            compressedBytes = os.toByteArray()
            compressedMimeType = "image/jpeg"
        }

        withContext(Dispatchers.Main) {
            if (compressedBytes.size <= IMAGE_MAX_FILE_SIZE) {
                val extension = MimeTypeMap.getSingleton()
                    .getExtensionFromMimeType(compressedMimeType)
                onImage(ImageData("image.${extension}", compressedMimeType, compressedBytes))
            } else {
                throw IOException(contextWrapper.getString(R.string.image_failure_file_size))
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
