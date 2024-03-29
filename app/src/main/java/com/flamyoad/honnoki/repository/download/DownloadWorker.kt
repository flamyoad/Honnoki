package com.flamyoad.honnoki.repository.download

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.work.*
import com.flamyoad.honnoki.R
import com.flamyoad.honnoki.data.db.AppDatabase
import com.flamyoad.honnoki.data.entities.Chapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.BufferedSink
import okio.buffer
import okio.sink
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named
import java.io.File

class DownloadWorker(context: Context, parameters: WorkerParameters) :
    CoroutineWorker(context, parameters), KoinComponent {

    private val db: AppDatabase by inject()

    private val okHttpClient: OkHttpClient by inject(named(inputData.getString(SOURCE) ?: ""))

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as
            NotificationManager

    override suspend fun doWork(): Result {
        val chapterIdList = inputData.getLongArray(CHAPTER_ID_LIST) ?: return Result.failure()
        val downloadRootDir = inputData.getString(DOWNLOAD_DIR_PATH) ?: return Result.failure()

        for (id in chapterIdList) {
            val chapter = db.chapterDao().get(id) ?: continue
            val overview =
                db.mangaOverviewDao().getByIdBlocking(chapter.mangaOverviewId) ?: continue
            // todo: refactor this method which extracts folder path
            val chapterDir = downloadRootDir + "/" + overview.mainTitle + "/" + chapter.title

            //todo: If user has not entered the reader before, page is always []. Fix!!
            val pages = db.pageDao().getPages(id)
            for (page in pages) {
                download(
                    page.link,
                    dirPath = chapterDir,
                    fileName = page.number.toString() + ".jpg"
                )
            }
        }
        return Result.success()
    }

    private suspend fun download(imageUrl: String, dirPath: String, fileName: String) {
        withContext(Dispatchers.IO) {
            // Calls setForegroundInfo() periodically when it needs to update
            // the ongoing Notification
            createDirectory(dirPath)

            val request = Request.Builder().url(imageUrl).build()
            val response = okHttpClient.newCall(request).execute()
            val downloadedFile = File(dirPath, fileName)
            val sink: BufferedSink = downloadedFile.sink().buffer()
            sink.writeAll(response.body!!.source())
            sink.close()

            setForeground(createForegroundInfo(fileName))
        }
    }

    // Creates an instance of ForegroundInfo which can be used to update the
    // ongoing notification.
    private fun createForegroundInfo(progress: String): ForegroundInfo {
//        val id = applicationContext.getString(R.string.notification_channel_id)
//        val title = applicationContext.getString(R.string.notification_title)
//        val cancel = applicationContext.getString(R.string.cancel_download)

        val title = "Download in progress"
        val cancel = "Cancel"

        // This PendingIntent can be used to cancel the worker
        val intent = WorkManager.getInstance(applicationContext)
            .createCancelPendingIntent(getId())

        // Create a Notification channel if necessary
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel()
        }

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle(title)
            .setTicker(title)
            .setContentText(progress)
            .setSmallIcon(R.drawable.aya)
            .setSilent(true) // Disables ringing sound
            .setOngoing(true)
            // Add the cancel action to the notification which can
            // be used to cancel the worker
            .addAction(android.R.drawable.ic_delete, cancel, intent)
            .build()

        return ForegroundInfo(NOTIFICATION_ID, notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel() {
//        val name = getString(R.string.channel_name)
//        val descriptionText = getString(R.string.channel_description)

        val name = "Downloader notification channel"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = "Downloader"
        }
        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        notificationManager.createNotificationChannel(channel)
    }

    private fun createDirectory(dir: String) {
        File(dir).also { it.mkdirs() }
    }

    companion object {
        private const val CHANNEL_ID = "honnoki.download"
        private const val NOTIFICATION_ID = 100

        private const val CHAPTER_ID_LIST = "chapter_id_list"
        private const val DOWNLOAD_DIR_PATH = "download_dir_path"
        private const val SOURCE = "source"

        // LongArray   is mapped to Java's primitive array long[]
        // Array<Long> is mapped to Java's boxed Long[]
        // cannot use toTypedArray() because it creates Array<Long>
        fun createRequiredData(
            chapters: List<Chapter>,
            downloadDirPath: String,
            source: String
        ): Data {
            val chapterIdArray = LongArray(chapters.size) { index ->
                chapters[index].id ?: -1
            }
            return Data.Builder().apply {
                putLongArray(CHAPTER_ID_LIST, chapterIdArray)
                putString(DOWNLOAD_DIR_PATH, downloadDirPath)
                putString(SOURCE, source)
            }.build()
        }
    }
}