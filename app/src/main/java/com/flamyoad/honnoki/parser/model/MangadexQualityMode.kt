package com.flamyoad.honnoki.parser.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
enum class MangadexQualityMode(val value: String): Parcelable {
    DATA("data"),
    DATA_SAVER("data-saver")
}