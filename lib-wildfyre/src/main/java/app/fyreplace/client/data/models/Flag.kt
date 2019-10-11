package app.fyreplace.client.data.models

import android.os.Parcel
import android.os.Parcelable

data class Flag(
    val reason: String,
    val comment: String? = null
) : Model {
    private constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(reason)
        parcel.writeString(comment)
    }

    companion object CREATOR : Parcelable.Creator<Flag> {
        override fun createFromParcel(parcel: Parcel) = Flag(parcel)

        override fun newArray(size: Int) = arrayOfNulls<Flag>(size)
    }
}

data class Choice(
    val key: Long,
    val value: String
) : Model {
    private constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(key)
        parcel.writeString(value)
    }

    companion object CREATOR : Parcelable.Creator<Choice> {
        override fun createFromParcel(parcel: Parcel) = Choice(parcel)

        override fun newArray(size: Int) = arrayOfNulls<Choice>(size)
    }
}
