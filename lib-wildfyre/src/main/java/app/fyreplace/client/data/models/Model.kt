package app.fyreplace.client.data.models

import android.os.Parcelable

interface Model : Parcelable {
    override fun describeContents() = 0
}
