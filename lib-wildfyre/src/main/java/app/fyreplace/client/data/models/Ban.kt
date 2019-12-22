package app.fyreplace.client.data.models

import android.os.Parcel
import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.*

@JsonClass(generateAdapter = true)
data class Ban(
    val timestamp: Date,
    val reason: Long,
    val comment: String?,
    val expiry: Date,
    val auto: Boolean?,
    @Json(name = "ban_all")
    val banAll: Boolean?,
    @Json(name = "ban_post")
    val banPost: Boolean?,
    @Json(name = "ban_comment")
    val banComment: Boolean?,
    @Json(name = "ban_flag")
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
