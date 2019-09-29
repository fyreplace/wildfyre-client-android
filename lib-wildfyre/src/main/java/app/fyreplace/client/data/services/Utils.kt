package app.fyreplace.client.data.services

import app.fyreplace.client.data.models.ImageData
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody

fun createFormData(name: String, image: ImageData): MultipartBody.Part =
    MultipartBody.Part.createFormData(
        name,
        image.fileName,
        RequestBody.create(MediaType.parse(image.mimeType), image.bytes)
    )
