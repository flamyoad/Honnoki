package com.flamyoad.honnoki.db.typeconverters

import android.net.Uri
import androidx.room.TypeConverter

class UriConverter {

    @TypeConverter
    fun toString(uri: Uri): String {
        return uri.toString()
    }

    @TypeConverter
    fun toUri(string: String): Uri {
        return Uri.parse(string)
    }
}