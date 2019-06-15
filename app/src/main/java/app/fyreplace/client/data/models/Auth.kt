package app.fyreplace.client.data.models

import java.io.Serializable

data class Auth(
    val username: String,
    val password: String
) : Serializable

data class AuthToken(
    val token: String
) : Serializable

data class Registration(
    val username: String,
    val email: String,
    val password: String,
    val captcha: String
) : Serializable

class RegistrationResult : Serializable
