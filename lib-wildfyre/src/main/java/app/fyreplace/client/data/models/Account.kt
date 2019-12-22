package app.fyreplace.client.data.models

import android.os.Parcel
import android.os.Parcelable
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Account(
    val id: Long,
    val username: String,
    val email: String?
) : Model {
    private constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString()!!,
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(username)
        parcel.writeString(email)
    }

    companion object CREATOR : Parcelable.Creator<Account> {
        override fun createFromParcel(parcel: Parcel) = Account(parcel)

        override fun newArray(size: Int) = arrayOfNulls<Account>(size)
    }
}

@JsonClass(generateAdapter = true)
data class AccountPatch(
    val email: String?,
    val password: String?
) : Model {
    private constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(email)
        parcel.writeString(password)
    }

    companion object CREATOR : Parcelable.Creator<AccountPatch> {
        override fun createFromParcel(parcel: Parcel) = AccountPatch(parcel)

        override fun newArray(size: Int) = arrayOfNulls<AccountPatch>(size)
    }
}
