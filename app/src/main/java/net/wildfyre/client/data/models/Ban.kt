package net.wildfyre.client.data.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*

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
