package com.flamyoad.honnoki.db

import androidx.room.migration.Migration
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.flamyoad.honnoki.data.db.AppDatabase
import com.flamyoad.honnoki.data.db.migrations.MIGRATION_2_TO_3
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class RoomMigrationTest {
    private val db = "test-database"

    @get:Rule
    val testHelper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        AppDatabase::class.java.canonicalName,
        FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    @Throws(IOException::class)
    fun testAllMigration() {
        testHelper.createDatabase(db, 2)
        testHelper.runMigrationsAndValidate(db, 3, true, MIGRATION_2_TO_3)
    }

    @Test
    @Throws(IOException::class)
    fun migrateFrom2To3() {
        testHelper.createDatabase(db, 2)
        testHelper.runMigrationsAndValidate(db, 3, true, MIGRATION_2_TO_3)
    }

}