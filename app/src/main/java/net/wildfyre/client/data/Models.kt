package net.wildfyre.client.data

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*

data class Account(
    val id: Long,
    val username: String,
    val email: String? = null
) : Serializable

data class AccountPatch(
    val email: String? = null,
    val password: String? = null
) : Serializable

data class Area(
    val name: String,
    val displayname: String
) : Serializable

data class Auth(
    val username: String,
    val password: String
) : Serializable

data class AuthToken(
    val token: String
) : Serializable

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
) : Serializable

data class Choice(
    val key: Long,
    val value: String
) : Serializable

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

data class Flag(
    val reason: String,
    val comment: String? = null
) : Serializable

data class Image(
    val num: Int,
    val image: String,
    val comment: String? = null
) : Serializable

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

data class RecoverTransaction(
    val transaction: String
) : Serializable

data class PasswordRecoveryStep1(
    val email: String,
    val password: String,
    val captcha: String
) : Serializable

data class PasswordRecoveryStep2(
    @SerializedName("new_password")
    val newPassword: String,
    val token: String,
    val transaction: String,
    val captcha: String
) : Serializable

data class Registration(
    val username: String,
    val email: String,
    val password: String,
    val captcha: String
) : Serializable

data class Spread(
    val spread: Boolean
) : Serializable

data class Subscription(
    val subscribed: Boolean
) : Serializable

class RegistrationResult : Serializable

data class UsernameRecovery(
    val email: String,
    val captcha: String
) : Serializable

data class Reputation(
    val reputation: Int,
    val spread: Int
) : Serializable

class Reset : Serializable

data class SuperItem<T>(
    val count: Int,
    val next: String? = null,
    val previous: String? = null,
    val results: List<T>
) : Serializable

typealias SuperBan = SuperItem<Ban>
typealias SuperNotification = SuperItem<Notification>
typealias SuperPost = SuperItem<Post>
