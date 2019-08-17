package app.fyreplace.client.data.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Area(
    val name: String,
    @SerializedName("displayname")
    val displayName: String
) : Serializable

data class Reputation(
    val reputation: Int,
    val spread: Int
) : Serializable


