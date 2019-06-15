package app.fyreplace.client.data.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*

data class Image(
    val num: Int,
    val image: String,
    val comment: String? = null
) : Serializable

data class Post(
    val id: Long,
    val author: Author? = null,
    val text: String? = null,
    val anonym: Boolean,
    val subscribed: Boolean,
    val created: Date,
    val active: Boolean,
    val image: String? = null,
    @SerializedName("additional_images")
    val additionalImages: List<Image>? = null,
    val comments: List<Comment>
) : Serializable

data class Spread(
    val spread: Boolean
) : Serializable

data class Subscription(
    val subscribed: Boolean
) : Serializable
