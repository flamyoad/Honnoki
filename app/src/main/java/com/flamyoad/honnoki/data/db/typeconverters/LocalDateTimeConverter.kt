package com.flamyoad.honnoki.data.db.typeconverters

import androidx.room.TypeConverter
import java.time.LocalDateTime

object LocalDateTimeConverter {
    @TypeConverter
    fun toDate(dateString: String): LocalDateTime {
        return LocalDateTime.parse(dateString)
    }

    @TypeConverter
    fun toDateString(date: LocalDateTime): String {
        return date.toString()
    }
}