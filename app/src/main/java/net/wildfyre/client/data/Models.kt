package net.wildfyre.client.data

import com.google.gson.annotations.SerializedName
import java.util.*

class Account {
    var id: Long? = null
    var username: String? = null
    var email: String? = null
}

class AccountPatch {
    var email: String? = null
    var password: String? = null
}

class Area {
    var name: String? = null
    var displayname: String? = null
}

class Auth {
    var username: String? = null
    var password: String? = null
}

class AuthToken {
    var token: String? = null
}

class Author {
    var user: Long? = null
    var name: String? = null
    var avatar: String? = null
    var bio: String? = null
    var banned: Boolean? = null
}

class Ban {
    var timestamp: Date? = null
    var reason: Long? = null
    var comment: String? = null
    var expiry: Date? = null
    var auto: Boolean? = null
    @SerializedName("ban_all")
    var banAll: Boolean? = null
    @SerializedName("ban_post")
    var banPost: Boolean? = null
    @SerializedName("ban_comment")
    var banComment: Boolean? = null
    @SerializedName("ban_flag")
    var banFlag: Boolean? = null
}

class Choice {
    var key: Long? = null
    var value: String? = null
}

class Comment {
    var id: Long? = null
    var author: Author? = null
    var created: Date? = null
    var text: String? = null
    var image: String? = null
}

class CommentData {
    var comment: String? = null
    var image: Any? = null
}

class Flag {
    var reason: String? = null
    var comment: String? = null
}

class Image {
    var num: Int? = null
    var image: String? = null
    var comment: String? = null
}

class Link {
    var url: String? = null
    var description: String? = null
    var author: String? = null
}

class Notification {
    var area: String? = null
    var post: NotificationPost? = null
    var comments: List<Long>? = null
}

open class NotificationPost {
    var id: Long? = null
    var author: Author? = null
    var text: String? = null
}

class Password

class Post : NotificationPost() {
    var anonym: Boolean? = null
    var subscribed: Boolean? = null
    var created: Date? = null
    var active: Boolean? = null
    var image: String? = null
    @SerializedName("additional_images")
    var additionalImages: List<Image>? = null
    var comments: List<Comment>? = null
}

class RecoverTransaction {
    var transaction: String? = null
}

class PasswordRecoveryStep1 {
    var email: String? = null
    var password: String? = null
    var captcha: String? = null
}

class PasswordRecoveryStep2 {
    @SerializedName("new_password")
    var newPassword: String? = null
    var token: String? = null
    var transaction: String? = null
    var captcha: String? = null
}

class Registration {
    var username: String? = null
    var email: String? = null
    var password: String? = null
    var captcha: String? = null
}

class Spread {
    var spread: Boolean? = null
}

class Subscription {
    var subscribed: Boolean? = null
}

class RegistrationResult

class UsernameRecovery {
    var email: String? = null
    var captcha: String? = null
}

class Reputation {
    var reputation: Int? = null
    var spread: Int? = null
}

class Reset

class SuperItem<T> {
    var count: Int? = null
    var next: String? = null
    var previous: String? = null
    var results: List<T>? = null
}

typealias SuperBan = SuperItem<Ban>
typealias SuperNotification = SuperItem<Notification>
typealias SuperPost = SuperItem<Post>
