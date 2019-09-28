package app.fyreplace.client.data.models

import java.io.Serializable

data class Account(
    val id: Long,
    val username: String,
    val email: String? = null
) : Serializable

data class AccountPatch(
    val email: String? = null,
    val password: String? = null
) : Serializable
