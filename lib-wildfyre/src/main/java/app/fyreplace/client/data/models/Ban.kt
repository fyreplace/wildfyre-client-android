package app.fyreplace.client.data.models

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import java.util.*

data class Ban(
    val timestamp: Date,
    val reason: Long,
    val comment: String? = null,
    val expiry: Date,
    val auto: Boolean? = null,
    @SerializedName("ban_all")
    val banAll: Boolean? = null,
    @SerializedName("ban_post")
    val banPost: Boolean? = null,
    @SerializedName("ban_comment")
    val banComment: Boolean? = null,
    @SerializedName("ban_flag")
    val banFlag: Boolean
) : Model {
    private constructor(parcel: Parcel) : this(
        Date(parcel.readLong()),
        parcel.readLong(),
        parcel.readString(),
        Date(parcel.readLong()),
        parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
        parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
        parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
        parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
        parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(timestamp.time)
        parcel.writeLong(reason)
        parcel.writeString(comment)
        parcel.writeLong(expiry.time)
        parcel.writeValue(auto)
        parcel.writeValue(banAll)
        parcel.writeValue(banPost)
        parcel.writeValue(banComment)
        parcel.writeByte(if (banFlag) 1 else 0)
    }

    companion object CREATOR : Parcelable.Creator<Ban> {
        override fun createFromParcel(parcel: Parcel) = Ban(parcel)

        override fun newArray(size: Int) = arrayOfNulls<Ban>(size)
    }
}
