package app.fyreplace.client.data.models

import java.io.Serializable

data class Author(
    val user: Long,
    val name: String,
    val avatar: String? = null,
    val bio: String? = null,
    val banned: Boolean
) : Serializable

data class AuthorPatch(
    val bio: String
) : Serializable
