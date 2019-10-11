package app.fyreplace.client.data.models

import android.os.Parcel
import android.os.Parcelable

abstract class SuperItem<T : Parcelable>(
    val count: Int,
    val next: String? = null,
    val previous: String? = null,
    val results: List<T>
) : Model {
    final override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(count)
        parcel.writeString(next)
        parcel.writeString(previous)
        parcel.writeTypedList(results)
    }
}

class SuperBan(
    count: Int,
    next: String?,
    previous: String?,
    results: List<Ban>
) : SuperItem<Ban>(count, next, previous, results) {
    private constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.createTypedArrayList(Ban)!!
    )

    companion object CREATOR : Parcelable.Creator<SuperBan> {
        override fun createFromParcel(parcel: Parcel) = SuperBan(parcel)

        override fun newArray(size: Int) = arrayOfNulls<SuperBan>(size)
    }
}

class SuperNotification(
    count: Int,
    next: String?,
    previous: String?,
    results: List<Notification>
) : SuperItem<Notification>(count, next, previous, results) {
    private constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.createTypedArrayList(Notification)!!
    )

    companion object CREATOR : Parcelable.Creator<SuperNotification> {
        override fun createFromParcel(parcel: Parcel) = SuperNotification(parcel)

        override fun newArray(size: Int) = arrayOfNulls<SuperNotification>(size)
    }
}

class SuperPost(
    count: Int,
    next: String?,
    previous: String?,
    results: List<Post>
) : SuperItem<Post>(count, next, previous, results) {
    private constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.createTypedArrayList(Post)!!
    )

    companion object CREATOR : Parcelable.Creator<SuperPost> {
        override fun createFromParcel(parcel: Parcel) = SuperPost(parcel)

        override fun newArray(size: Int) = arrayOfNulls<SuperPost>(size)
    }
}
