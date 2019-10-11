package app.fyreplace.client.data.models

import android.os.Parcel
import android.os.Parcelable

data class Auth(
    val username: String,
    val password: String
) : Model {
    private constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(username)
        parcel.writeString(password)
    }

    companion object CREATOR : Parcelable.Creator<Auth> {
        override fun createFromParcel(parcel: Parcel) = Auth(parcel)

        override fun newArray(size: Int) = arrayOfNulls<Auth>(size)
    }
}

data class AuthToken(val token: String) : Model {
    private constructor(parcel: Parcel) : this(parcel.readString()!!)

    override fun writeToParcel(parcel: Parcel, flags: Int) = parcel.writeString(token)

    companion object CREATOR : Parcelable.Creator<AuthToken> {
        override fun createFromParcel(parcel: Parcel) = AuthToken(parcel)

        override fun newArray(size: Int) = arrayOfNulls<AuthToken>(size)
    }
}

data class Registration(
    val username: String,
    val email: String,
    val password: String,
    val captcha: String
) : Model {
    private constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(username)
        parcel.writeString(email)
        parcel.writeString(password)
        parcel.writeString(captcha)
    }

    companion object CREATOR : Parcelable.Creator<Registration> {
        override fun createFromParcel(parcel: Parcel) = Registration(parcel)

        override fun newArray(size: Int) = arrayOfNulls<Registration>(size)
    }
}

class RegistrationResult : Model {
    override fun writeToParcel(parcel: Parcel, flags: Int) = Unit

    companion object CREATOR : Parcelable.Creator<RegistrationResult> {
        override fun createFromParcel(parcel: Parcel) = RegistrationResult()

        override fun newArray(size: Int) = arrayOfNulls<RegistrationResult>(size)
    }
}
