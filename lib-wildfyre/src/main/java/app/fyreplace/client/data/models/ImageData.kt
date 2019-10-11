package app.fyreplace.client.data.models

import android.os.Parcel
import android.os.Parcelable

data class ImageData(
    val fileName: String,
    val mimeType: String,
    val bytes: ByteArray
) : Model {
    private constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.createByteArray()!!
    )

    override fun equals(other: Any?) = when {
        this === other -> true
        javaClass != other?.javaClass -> false
        else -> bytes.contentEquals((other as ImageData).bytes)
    }

    override fun hashCode() = bytes.contentHashCode()

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(fileName)
        parcel.writeString(mimeType)
        parcel.writeByteArray(bytes)
    }

    companion object CREATOR : Parcelable.Creator<ImageData> {
        override fun createFromParcel(parcel: Parcel) = ImageData(parcel)

        override fun newArray(size: Int) = arrayOfNulls<ImageData>(size)
    }
}
