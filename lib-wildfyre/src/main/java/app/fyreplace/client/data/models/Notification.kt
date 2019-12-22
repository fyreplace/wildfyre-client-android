package app.fyreplace.client.data.models

import android.os.Parcel
import android.os.Parcelable
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Notification(
    val area: String,
    val post: NotificationPost,
    val comments: LongArray
) : Model {
    private constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readParcelable(NotificationPost::class.java.classLoader)!!,
        parcel.createLongArray()!!
    )

    override fun equals(other: Any?) = when {
        this === other -> true
        javaClass != other?.javaClass -> false
        else -> {
            other as Notification

            when {
                area != other.area -> false
                post != other.post -> false
                !comments.contentEquals(other.comments) -> false
                else -> true
            }
        }
    }

    override fun hashCode(): Int {
        var result = area.hashCode()
        result = 31 * result + post.hashCode()
        result = 31 * result + comments.contentHashCode()
        return result
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(area)
        parcel.writeParcelable(post, flags)
        parcel.writeLongArray(comments)
    }

    companion object CREATOR : Parcelable.Creator<Notification> {
        override fun createFromParcel(parcel: Parcel) = Notification(parcel)

        override fun newArray(size: Int) = arrayOfNulls<Notification>(size)
    }
}

@JsonClass(generateAdapter = true)
data class NotificationPost(
    val id: Long,
    val author: Author?,
    val text: String?
) : Model {
    private constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readParcelable(Author::class.java.classLoader),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeParcelable(author, flags)
        parcel.writeString(text)
    }

    companion object CREATOR : Parcelable.Creator<NotificationPost> {
        override fun createFromParcel(parcel: Parcel) = NotificationPost(parcel)

        override fun newArray(size: Int) = arrayOfNulls<NotificationPost>(size)
    }
}
