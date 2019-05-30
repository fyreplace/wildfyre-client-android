package net.wildfyre.client.data

import com.google.gson.annotations.SerializedName
import java.util.*

data class Account(
    val id: Long,
    val username: String,
    val email: String? = null
)

data class AccountPatch(
    val email: String? = null,
    val password: String? = null
)

data class Area(
    val name: String,
    val displayname: String
)

data class Auth(
    val username: String,
    val password: String
)

data class AuthToken(
    val token: String
)

data class Author(
    val user: Long,
    val name: String,
    val avatar: String? = null,
    val bio: String? = null,
    val banned: Boolean
)

data class AuthorPatch(
    val bio: String
)

data class Ban(
    val timestamp: Date,
    val reason: Long,
    val comment: String? = null,
    val expiry: Date,
    val auto: Boolean? = null,
    @SerializedName("ban_all")
    val banAll: Boolean? = null,
    @SerializedName("ban_post")
    val banPost: Boolean? = null,
    @SerializedName("ban_comment")
    val banComment: Boolean? = null,
    @SerializedName("ban_flag")
    val banFlag: Boolean
)

data class Choice(
    val key: Long,
    val value: String
)

data class Comment(
    val id: Long,
    val author: Author? = null,
    val created: Date,
    val text: String? = null,
    val image: String? = null
)

data class CommentText(
    val text: String
)

data class Flag(
    val reason: String,
    val comment: String? = null
)

data class Image(
    val num: Int,
    val image: String,
    val comment: String? = null
)

data class Notification(
    val area: String,
    val post: NotificationPost,
    val comments: List<Long>
)

data class NotificationPost(
    val id: Long,
    val author: Author? = null,
    val text: String? = null
)

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
)

data class RecoverTransaction(
    val transaction: String
)

data class PasswordRecoveryStep1(
    val email: String,
    val password: String,
    val captcha: String
)

data class PasswordRecoveryStep2(
    @SerializedName("new_password")
    val newPassword: String,
    val token: String,
    val transaction: String,
    val captcha: String
)

data class Registration(
    val username: String,
    val email: String,
    val password: String,
    val captcha: String
)

data class Spread(
    val spread: Boolean
)

data class Subscription(
    val subscribed: Boolean
)

class RegistrationResult

data class UsernameRecovery(
    val email: String,
    val captcha: String
)

data class Reputation(
    val reputation: Int,
    val spread: Int
)

class Reset

data class SuperItem<T>(
    val count: Int,
    val next: String? = null,
    val previous: String? = null,
    val results: List<T>
)

typealias SuperBan = SuperItem<Ban>
typealias SuperNotification = SuperItem<Notification>
typealias SuperPost = SuperItem<Post>
