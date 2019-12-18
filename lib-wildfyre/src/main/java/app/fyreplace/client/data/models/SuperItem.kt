package app.fyreplace.client.data.models

import android.os.Parcel
import android.os.Parcelable

@Suppress("UNCHECKED_CAST")
open class SuperItem<T : Parcelable>(
    val count: Int,
    val next: String? = null,
    val previous: String? = null,
    val results: List<T>
) : Model {
    private constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(List::class.java.classLoader) as List<T>
    )

    final override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(count)
        parcel.writeString(next)
        parcel.writeString(previous)
        parcel.writeValue(results)
    }

    companion object CREATOR : Parcelable.Creator<SuperItem<*>> {
        override fun createFromParcel(parcel: Parcel) = SuperItem<Parcelable>(parcel)

        override fun newArray(size: Int) = arrayOfNulls<SuperItem<*>>(size)
    }
}

typealias SuperBan = SuperItem<Ban>

typealias SuperNotification = SuperItem<Notification>

typealias SuperPost = SuperItem<Post>
