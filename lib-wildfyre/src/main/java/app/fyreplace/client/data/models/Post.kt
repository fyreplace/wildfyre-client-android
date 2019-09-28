package app.fyreplace.client.data.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*

data class Draft(
    val text: String,
    @SerializedName("anonym")
    val anonymous: Boolean
) : Serializable

data class DraftNoImageContent(
    val text: String
) {
    val image = null
}

data class Image(
    val num: Int,
    val image: String,
    val comment: String? = null
) : Serializable

data class Post(
    val id: Long,
    val author: Author? = null,
    val text: String? = null,
    @SerializedName("anonym")
    val anonymous: Boolean,
    val subscribed: Boolean,
    val created: Date,
    val active: Boolean,
    val image: String? = null,
    @SerializedName("additional_images")
    val additionalImages: MutableList<Image> = mutableListOf(),
    val comments: List<Comment>
) : Serializable

data class Spread(
    val spread: Boolean
) : Serializable

data class Subscription(
    val subscribed: Boolean
) : Serializable