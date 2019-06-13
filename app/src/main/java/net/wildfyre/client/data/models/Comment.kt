package net.wildfyre.client.data.models

import java.io.Serializable
import java.util.*

data class Comment(
    val id: Long,
    val author: Author? = null,
    val created: Date,
    val text: String? = null,
    val image: String? = null
) : Serializable

data class CommentText(
    val text: String
) : Serializable
