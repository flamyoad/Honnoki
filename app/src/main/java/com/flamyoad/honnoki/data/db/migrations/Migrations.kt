package com.flamyoad.honnoki.data.db.migrations

import androidx.room.migration.Migration

val MIGRATION_2_TO_3 = Migration(2, 3) { database ->
    with(database) {
        execSQL("DROP TABLE page")
        execSQL("CREATE TABLE IF NOT EXISTS `page` (`id` INTEGER, `number` INTEGER NOT NULL, `chapterId` INTEGER, `link` TEXT NOT NULL, `linkDataSaver` TEXT, PRIMARY KEY(`id`), FOREIGN KEY(`chapterId`) REFERENCES `chapters`(`id`) ON UPDATE CASCADE ON DELETE CASCADE )")
        execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_page_chapterId_number` ON `page` (`chapterId`, `number`)")
    }
}