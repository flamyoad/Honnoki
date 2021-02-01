package com.flamyoad.honnoki.db.typeconverters

import androidx.room.TypeConverter
import com.flamyoad.honnoki.model.Source

class SourceConverter {
    @TypeConverter
    fun toString(source: Source): String {
        return source.name
    }

    @TypeConverter
    fun toSource(string: String): Source {
        return Source.valueOf(string)
    }
}