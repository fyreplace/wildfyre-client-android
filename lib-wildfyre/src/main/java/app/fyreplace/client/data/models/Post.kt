package app.fyreplace.client.data.models

import android.os.Parcel
import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.*

@JsonClass(generateAdapter = true)
data class Draft(
    val text: String,
    @Json(name = "anonym")
    val anonymous: Boolean
) : Model {
    private constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(text)
        parcel.writeByte(if (anonymous) 1 else 0)
    }

    companion object CREATOR : Parcelable.Creator<Draft> {
        override fun createFromParcel(parcel: Parcel) = Draft(parcel)

        override fun newArray(size: Int) = arrayOfNulls<Draft>(size)
    }
}

@JsonClass(generateAdapter = true)
data class DraftNoImageContent(val text: String) : Model {
    val image = null

    private constructor(parcel: Parcel) : this(parcel.readString()!!)

    override fun writeToParcel(parcel: Parcel, flags: Int) = parcel.writeString(text)

    companion object CREATOR : Parcelable.Creator<DraftNoImageContent> {
        override fun createFromParcel(parcel: Parcel) = DraftNoImageContent(parcel)

        override fun newArray(size: Int) = arrayOfNulls<DraftNoImageContent>(size)
    }
}

@JsonClass(generateAdapter = true)
data class Image(
    val num: Int,
    val image: String,
    val comment: String? = null
) : Model {
    private constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(num)
        parcel.writeString(image)
        parcel.writeString(comment)
    }

    companion object CREATOR : Parcelable.Creator<Image> {
        override fun createFromParcel(parcel: Parcel) = Image(parcel)

        override fun newArray(size: Int) = arrayOfNulls<Image>(size)
    }
}

@JsonClass(generateAdapter = true)
data class Post(
    val id: Long,
    val author: Author? = null,
    val text: String,
    @Json(name = "anonym")
    val anonymous: Boolean,
    val subscribed: Boolean,
    val created: Date,
    val active: Boolean,
    val image: String? = null,
    @Json(name = "additional_images")
    val additionalImages: MutableList<Image> = mutableListOf(),
    val comments: List<Comment>
) : Model {
    private constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readParcelable(Author::class.java.classLoader),
        parcel.readString()!!,
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        Date(parcel.readLong()),
        parcel.readByte() != 0.toByte(),
        parcel.readString(),
        parcel.createTypedArrayList(Image)!!,
        parcel.createTypedArrayList(Comment)!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeParcelable(author, flags)
        parcel.writeString(text)
        parcel.writeByte(if (anonymous) 1 else 0)
        parcel.writeByte(if (subscribed) 1 else 0)
        parcel.writeLong(created.time)
        parcel.writeByte(if (active) 1 else 0)
        parcel.writeString(image)
        parcel.writeTypedList(additionalImages)
        parcel.writeTypedList(comments)
    }

    companion object CREATOR : Parcelable.Creator<Post> {
        override fun createFromParcel(parcel: Parcel) = Post(parcel)

        override fun newArray(size: Int) = arrayOfNulls<Post>(size)
    }
}

@JsonClass(generateAdapter = true)
data class Spread(val spread: Boolean) : Model {
    private constructor(parcel: Parcel) : this(parcel.readByte() != 0.toByte())

    override fun writeToParcel(parcel: Parcel, flags: Int) = parcel.writeByte(if (spread) 1 else 0)

    companion object CREATOR : Parcelable.Creator<Spread> {
        override fun createFromParcel(parcel: Parcel) = Spread(parcel)

        override fun newArray(size: Int) = arrayOfNulls<Spread>(size)
    }
}

@JsonClass(generateAdapter = true)
data class Subscription(val subscribed: Boolean) : Model {
    private constructor(parcel: Parcel) : this(parcel.readByte() != 0.toByte())

    override fun writeToParcel(parcel: Parcel, flags: Int) =
        parcel.writeByte(if (subscribed) 1 else 0)

    companion object CREATOR : Parcelable.Creator<Subscription> {
        override fun createFromParcel(parcel: Parcel) = Subscription(parcel)

        override fun newArray(size: Int) = arrayOfNulls<Subscription>(size)
    }
}
