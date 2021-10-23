package com.flamyoad.honnoki.repository.download

import android.content.Context
import androidx.work.*
import com.flamyoad.honnoki.data.db.AppDatabase
import com.flamyoad.honnoki.data.entities.Chapter
import com.flamyoad.honnoki.data.preference.DownloadPreference
import com.flamyoad.honnoki.utils.extensions.tryOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DownloadRepository(
    private val db: AppDatabase,
    private val downloadPreference: DownloadPreference,
    private val context: Context
) {

    // todo:
    //  1. how to let user pause and continue donwload
    //  2. is multiple download possible?
    suspend fun downloadChapters(chapters: List<Chapter>) {
        if (chapters.isEmpty()) return
        val workManager = getWorkManager() ?: return

        // todo: Check if can download using user chosen directory
        //  currently using Android/data
        val data = withContext(Dispatchers.IO) {
            val downloadDir =
                downloadPreference.getDownloadDirectoryPath().let {
                    if (it.isEmpty()) {
                        context.getExternalFilesDir("").toString()
                    } else {
                        it
                    }
                }
            val overview = db.mangaOverviewDao()
                .getByIdBlocking(chapters.first().mangaOverviewId)
            val source = overview?.source

            return@withContext DownloadWorker.createRequiredData(
                chapters = chapters,
                downloadDirPath = downloadDir,
                source = source.toString()
            )
        }

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<DownloadWorker>()
            .setInputData(data)
            .setConstraints(constraints)
            .build()

        //todo: Find a way to set uniqueness!
        // Is it better to set one work per chapter?
        // but workmanager seem to have hard limit for number of jobs
        // Concern: multiple downloads possible?
        workManager.enqueueUniqueWork(
            "unique",
            ExistingWorkPolicy.KEEP,
            workRequest
        )
    }

    /**
     * Throws: IllegalStateException – If WorkManager is not initialized properly
     */
    private fun getWorkManager(): WorkManager? =
        tryOrNull { WorkManager.getInstance(context) }
}