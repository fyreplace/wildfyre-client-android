package net.wildfyre.client.data

import java.util.*

class Account {
    var id: Long? = null
    var username: String? = null
    var email: String? = null

    class Auth {
        var username: String? = null
        var password: String? = null
    }

    class Patch {
        var email: String? = null
        var password: String? = null
    }

    class Registration {
        var username: String? = null
        var email: String? = null
        var password: String? = null
        var captcha: String? = null
    }

    class PasswordRecoveryStep1 {
        var email: String? = null
        var password: String? = null
        var captcha: String? = null
    }

    class PasswordRecoveryStep2 {
        var new_password: String? = null
        var token: String? = null
        var transaction: String? = null
        var captcha: String? = null
    }

    class UsernameRecovery {
        var email: String? = null
        var captcha: String? = null
    }
}

class Area {
    var name: String? = null
    var displayname: String? = null
    var rep: Long? = null
    var spread: Long? = null
}

class Auth {
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
    var ban_all: Boolean? = null
    var ban_post: Boolean? = null
    var ban_comment: Boolean? = null
    var ban_flag: Boolean? = null
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
    var num: Long? = null
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
    var additional_images: List<Image>? = null
    var comments: List<Comment>? = null

    class Spread {
        var spread: Boolean? = null
    }

    class Subscription {
        var subscribed: Boolean? = null
    }
}

class RecoverTransaction {
    var transaction: String? = null
}

class Registration

class Reputation {
    var reputation: Long? = null
    var spread: Long? = null
}

class Reset

class SuperItem<T> {
    var count: Long? = null
    var next: String? = null
    var previous: String? = null
    var results: List<T>? = null
}

typealias SuperBan = SuperItem<Ban>
typealias SuperNotification = SuperItem<Notification>
typealias SuperPost = SuperItem<Post>
