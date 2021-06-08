package com.flamyoad.honnoki.data.db

import android.content.res.Resources
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.flamyoad.honnoki.R

class DatabasePrePopulateCallback(private val resources: Resources) : RoomDatabase.Callback() {

    override fun onCreate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "INSERT INTO bookmark_group (id, name) VALUES (?,?)",
            arrayOf(null, resources.getString(R.string.default_group_name))
        )
    }
}