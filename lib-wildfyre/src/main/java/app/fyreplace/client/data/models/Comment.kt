package app.fyreplace.client.data.models

import android.os.Parcel
import android.os.Parcelable
import com.squareup.moshi.JsonClass
import java.util.*

@JsonClass(generateAdapter = true)
data class Comment(
    val id: Long,
    val author: Author? = null,
    val created: Date,
    val text: String? = null,
    val image: String? = null
) : Model {
    private constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readParcelable(Author::class.java.classLoader),
        Date(parcel.readLong()),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeParcelable(author, flags)
        parcel.writeLong(created.time)
        parcel.writeString(text)
        parcel.writeString(image)
    }

    companion object CREATOR : Parcelable.Creator<Comment> {
        override fun createFromParcel(parcel: Parcel) = Comment(parcel)

        override fun newArray(size: Int) = arrayOfNulls<Comment>(size)
    }
}

@JsonClass(generateAdapter = true)
data class CommentText(val text: String) : Model {
    private constructor(parcel: Parcel) : this(parcel.readString()!!)

    override fun writeToParcel(parcel: Parcel, flags: Int) = parcel.writeString(text)

    companion object CREATOR : Parcelable.Creator<CommentText> {
        override fun createFromParcel(parcel: Parcel) = CommentText(parcel)

        override fun newArray(size: Int) = arrayOfNulls<CommentText>(size)
    }
}
