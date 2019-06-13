package net.wildfyre.client.data.models

import java.io.Serializable

data class Notification(
    val area: String,
    val post: NotificationPost,
    val comments: List<Long>
) : Serializable

data class NotificationPost(
    val id: Long,
    val author: Author? = null,
    val text: String? = null
) : Serializable
