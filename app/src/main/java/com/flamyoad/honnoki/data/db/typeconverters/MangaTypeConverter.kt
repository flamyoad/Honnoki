package com.flamyoad.honnoki.data.db.typeconverters

import androidx.room.TypeConverter
import com.flamyoad.honnoki.data.entities.MangaType

class MangaTypeConverter {
    @TypeConverter
    fun toString(mangaType: MangaType): String {
        return mangaType.name
    }

    @TypeConverter
    fun toSource(string: String): MangaType {
        return MangaType.valueOf(string)
    }
}