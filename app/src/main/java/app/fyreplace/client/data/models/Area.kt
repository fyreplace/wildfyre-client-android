package app.fyreplace.client.data.models

import java.io.Serializable

data class Area(
    val name: String,
    val displayname: String
) : Serializable

data class Reputation(
    val reputation: Int,
    val spread: Int
) : Serializable


