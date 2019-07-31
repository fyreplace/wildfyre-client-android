package app.fyreplace.client.data.models

import java.io.Serializable

data class ImageData(val fileName: String, val mimeType: String, val bytes: ByteArray) : Serializable {
    override fun equals(other: Any?) = when {
        this === other -> true
        javaClass != other?.javaClass -> false
        else -> bytes.contentEquals((other as ImageData).bytes)
    }

    override fun hashCode() = bytes.contentHashCode()
}
