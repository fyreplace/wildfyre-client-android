package app.fyreplace.client.data.models

import java.io.Serializable

data class SuperItem<T>(
    val count: Int,
    val next: String? = null,
    val previous: String? = null,
    val results: List<T>
) : Serializable

typealias SuperBan = SuperItem<Ban>
typealias SuperNotification = SuperItem<Notification>
typealias SuperPost = SuperItem<Post>
