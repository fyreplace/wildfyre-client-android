package net.wildfyre.client.data

import java.util.*

abstract class ApiItem {
    var _body: String? = null
}

class Account : ApiItem() {
    var id: Long? = null
    var username: String? = null
    var email: String? = null

    class Auth : ApiItem() {
        var username: String? = null
        var password: String? = null
    }

    class Patch : ApiItem() {
        var email: String? = null
        var password: String? = null
    }

    class Registration : ApiItem() {
        var username: String? = null
        var email: String? = null
        var password: String? = null
        var captcha: String? = null
    }

    class PasswordRecoveryStep1 : ApiItem() {
        var email: String? = null
        var password: String? = null
        var captcha: String? = null
    }

    class PasswordRecoveryStep2 : ApiItem() {
        var new_password: String? = null
        var token: String? = null
        var transaction: String? = null
        var captcha: String? = null
    }

    class UsernameRecovery : ApiItem() {
        var email: String? = null
        var captcha: String? = null
    }
}

class Area : ApiItem() {
    var name: String? = null
    var displayname: String? = null
    var rep: Long? = null
    var spread: Long? = null
}

class Auth : ApiItem() {
    var token: String? = null
}

class Author : ApiItem() {
    var user: Long? = null
    var name: String? = null
    var avatar: String? = null
    var bio: String? = null
    var banned: Boolean? = null
}

class Ban : ApiItem() {
    var timestamp: Date? = null
    var reason: Long? = null
    var comment: String? = null
    var expiry: Date? = null
    var auto: Boolean? = null
    var ban_all: Boolean? = null
    var ban_post: Boolean? = null
    var ban_comment: Boolean? = null
    var ban_flag: Boolean? = null
}

class Choice : ApiItem() {
    var key: Long? = null
    var value: String? = null
}

class Comment : ApiItem() {
    var id: Long? = null
    var author: Author? = null
    var created: Date? = null
    var text: String? = null
    var image: String? = null
}

class CommentData : ApiItem() {
    var comment: String? = null
    var image: Any? = null
}

class Flag : ApiItem() {
    var reason: String? = null
    var comment: String? = null
}

class Image : ApiItem() {
    var num: Long? = null
    var image: String? = null
    var comment: String? = null
}

class Link : ApiItem() {
    var url: String? = null
    var description: String? = null
    var author: String? = null
}

class Notification : ApiItem() {
    var area: String? = null
    var post: NotificationPost? = null
    var comments: List<Long>? = null
}

open class NotificationPost : ApiItem() {
    var id: Long? = null
    var author: Author? = null
    var text: String? = null
}

class Password : ApiItem()

class Post : NotificationPost() {
    var anonym: Boolean? = null
    var subscribed: Boolean? = null
    var created: Date? = null
    var active: Boolean? = null
    var image: String? = null
    var additional_images: List<Image>? = null
    var comments: List<Comment>? = null

    class Spread : ApiItem() {
        var spread: Boolean? = null
    }

    class Subscription : ApiItem() {
        var subscribed: Boolean? = null
    }
}

class RecoverTransaction : ApiItem() {
    var transaction: String? = null
}

class Registration : ApiItem()

class Reputation : ApiItem() {
    var reputation: Long? = null
    var spread: Long? = null
}

class Reset : ApiItem()

class SuperItem<T> : ApiItem() {
    var count: Long? = null
    var next: String? = null
    var previous: String? = null
    var results: List<T>? = null
}

typealias SuperBan = SuperItem<Ban>
typealias SuperNotification = SuperItem<Notification>
typealias SuperPost = SuperItem<Post>