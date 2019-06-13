package net.wildfyre.client.data.models

import java.io.Serializable

data class Flag(
    val reason: String,
    val comment: String? = null
) : Serializable

data class Choice(
    val key: Long,
    val value: String
) : Serializable
