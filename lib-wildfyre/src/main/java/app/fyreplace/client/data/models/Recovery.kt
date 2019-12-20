package app.fyreplace.client.data.models

import android.os.Parcel
import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PasswordRecoveryStep1(
    val email: String,
    val username: String,
    val captcha: String
) : Model {
    private constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(email)
        parcel.writeString(username)
        parcel.writeString(captcha)
    }

    companion object CREATOR : Parcelable.Creator<PasswordRecoveryStep1> {
        override fun createFromParcel(parcel: Parcel) = PasswordRecoveryStep1(parcel)

        override fun newArray(size: Int) = arrayOfNulls<PasswordRecoveryStep1>(size)
    }
}

@JsonClass(generateAdapter = true)
data class PasswordRecoveryStep2(
    @Json(name = "new_password")
    val newPassword: String,
    val token: String,
    val transaction: String,
    val captcha: String
) : Model {
    private constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(newPassword)
        parcel.writeString(token)
        parcel.writeString(transaction)
        parcel.writeString(captcha)
    }

    companion object CREATOR : Parcelable.Creator<PasswordRecoveryStep2> {
        override fun createFromParcel(parcel: Parcel) = PasswordRecoveryStep2(parcel)

        override fun newArray(size: Int) = arrayOfNulls<PasswordRecoveryStep2>(size)
    }
}

@JsonClass(generateAdapter = true)
data class RecoverTransaction(val transaction: String) : Model {
    private constructor(parcel: Parcel) : this(parcel.readString()!!)

    override fun writeToParcel(parcel: Parcel, flags: Int) = parcel.writeString(transaction)

    companion object CREATOR : Parcelable.Creator<RecoverTransaction> {
        override fun createFromParcel(parcel: Parcel) = RecoverTransaction(parcel)

        override fun newArray(size: Int) = arrayOfNulls<RecoverTransaction>(size)
    }
}

@JsonClass(generateAdapter = true)
class Reset : Model {
    override fun writeToParcel(parcel: Parcel, flags: Int) = Unit

    companion object CREATOR : Parcelable.Creator<Reset> {
        override fun createFromParcel(parcel: Parcel) = Reset()

        override fun newArray(size: Int) = arrayOfNulls<Reset>(size)
    }
}

@JsonClass(generateAdapter = true)
data class UsernameRecovery(
    val email: String,
    val captcha: String
) : Model {
    private constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(email)
        parcel.writeString(captcha)
    }

    companion object CREATOR : Parcelable.Creator<UsernameRecovery> {
        override fun createFromParcel(parcel: Parcel) = UsernameRecovery(parcel)

        override fun newArray(size: Int) = arrayOfNulls<UsernameRecovery>(size)
    }
}
