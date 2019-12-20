package app.fyreplace.client.data.models

import android.os.Parcel
import android.os.Parcelable
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Flag(
    val reason: Long?,
    val comment: String? = null
) : Model {
    private constructor(parcel: Parcel) : this(
        parcel.readSerializable() as Long?,
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeSerializable(reason)
        parcel.writeString(comment)
    }

    companion object CREATOR : Parcelable.Creator<Flag> {
        override fun createFromParcel(parcel: Parcel) = Flag(parcel)

        override fun newArray(size: Int) = arrayOfNulls<Flag>(size)
    }
}

@JsonClass(generateAdapter = true)
data class Choice(
    val key: Long?,
    val value: String
) : Model {
    private constructor(parcel: Parcel) : this(
        parcel.readSerializable() as Long?,
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeSerializable(key)
        parcel.writeString(value)
    }

    companion object CREATOR : Parcelable.Creator<Choice> {
        override fun createFromParcel(parcel: Parcel) = Choice(parcel)

        override fun newArray(size: Int) = arrayOfNulls<Choice>(size)
    }
}
