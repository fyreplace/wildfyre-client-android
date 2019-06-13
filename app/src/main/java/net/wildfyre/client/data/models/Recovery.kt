package net.wildfyre.client.data.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

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

data class RecoverTransaction(
    val transaction: String
) : Serializable

class Reset : Serializable

data class UsernameRecovery(
    val email: String,
    val captcha: String
) : Serializable
