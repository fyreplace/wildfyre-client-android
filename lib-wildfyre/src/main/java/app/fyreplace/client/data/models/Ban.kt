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
    val expiry: Date?,
    val auto: Boolean?,
    @Json(name = "ban_all")
    val banAll: Boolean,
    @Json(name = "ban_post")
    val banPost: Boolean,
    @Json(name = "ban_comment")
    val banComment: Boolean,
    @Json(name = "ban_flag")
    val banFlag: Boolean
) : Model {
    private constructor(parcel: Parcel) : this(
        parcel.readDate(),
        parcel.readLong(),
        parcel.readString(),
        parcel.readNullableDate(),
        parcel.readNullableBool(),
        parcel.readBool(),
        parcel.readBool(),
        parcel.readBool(),
        parcel.readBool()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeDate(timestamp)
        parcel.writeLong(reason)
        parcel.writeString(comment)
        parcel.writeDate(expiry)
        parcel.writeBool(auto)
        parcel.writeBool(banAll)
        parcel.writeBool(banPost)
        parcel.writeBool(banComment)
        parcel.writeBool(banFlag)
    }

    companion object CREATOR : Parcelable.Creator<Ban> {
        override fun createFromParcel(parcel: Parcel) = Ban(parcel)

        override fun newArray(size: Int) = arrayOfNulls<Ban>(size)
    }
}
