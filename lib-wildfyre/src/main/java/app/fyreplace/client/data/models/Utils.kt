package app.fyreplace.client.data.models

import android.os.Parcel
import java.util.*

fun Parcel.readNullableBool() = when (readInt()) {
    0 -> null
    1 -> false
    else -> true
}

fun Parcel.readBool() = readInt() > 1

fun Parcel.writeBool(value: Boolean?) = writeInt(
    when (value) {
        null -> 0
        false -> 1
        true -> 2
    }
)

fun Parcel.readDate() = Date(readLong())

fun Parcel.readNullableDate() = readLong().let { if (it != -1L) Date(it) else null }

fun Parcel.writeDate(value: Date?) = writeLong(value?.time ?: -1L)
