package com.flamyoad.honnoki.repository.download

import android.content.Context
import androidx.work.*
import com.flamyoad.honnoki.data.db.AppDatabase
import com.flamyoad.honnoki.data.entities.Chapter
import com.flamyoad.honnoki.data.preference.DownloadPreference
import com.flamyoad.honnoki.utils.extensions.tryOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DownloadRepositoryImpl(
    private val db: AppDatabase,
    private val downloadPreference: DownloadPreference,
    private val context: Context
): DownloadRepository {

    // todo:
    //  1. how to let user pause and continue donwload
    override suspend fun downloadChapters(chapters: List<Chapter>) {
        if (chapters.isEmpty()) return
        chapters.forEach { downloadChapter(it) }
    }

    private suspend fun downloadChapter(chapter: Chapter) {
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
            val overview = db.mangaOverviewDao().getByIdBlocking(chapter.mangaOverviewId)
            val source = overview?.source

            return@withContext DownloadWorker.createRequiredData(
                chapter = chapter,
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

        workManager.enqueueUniqueWork(
            UNIQUE_WORKNAME,
            ExistingWorkPolicy.KEEP,
            workRequest
        )
    }

    /**
     * Throws: IllegalStateException – If WorkManager is not initialized properly
     */
    private fun getWorkManager(): WorkManager? =
        tryOrNull { WorkManager.getInstance(context) }

    companion object {
        private const val UNIQUE_WORKNAME = "download"
    }
}