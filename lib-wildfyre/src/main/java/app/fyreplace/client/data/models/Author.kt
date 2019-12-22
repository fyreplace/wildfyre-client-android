package app.fyreplace.client.data.models

import android.os.Parcel
import android.os.Parcelable
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Author(
    val user: Long,
    val name: String,
    val avatar: String?,
    val bio: String?,
    val banned: Boolean
) : Model {
    private constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString()!!,
        parcel.readString(),
        parcel.readString(),
        parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(user)
        parcel.writeString(name)
        parcel.writeString(avatar)
        parcel.writeString(bio)
        parcel.writeByte(if (banned) 1 else 0)
    }

    companion object CREATOR : Parcelable.Creator<Author> {
        override fun createFromParcel(parcel: Parcel): Author {
            return Author(parcel)
        }

        override fun newArray(size: Int): Array<Author?> {
            return arrayOfNulls(size)
        }
    }
}

@JsonClass(generateAdapter = true)
data class AuthorPatch(val bio: String) : Model {
    private constructor(parcel: Parcel) : this(parcel.readString()!!)

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(bio)
    }

    companion object CREATOR : Parcelable.Creator<AuthorPatch> {
        override fun createFromParcel(parcel: Parcel) = AuthorPatch(parcel)

        override fun newArray(size: Int) = arrayOfNulls<AuthorPatch>(size)
    }
}
