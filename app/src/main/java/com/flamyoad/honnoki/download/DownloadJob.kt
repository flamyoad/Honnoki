package com.flamyoad.honnoki.download

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.flamyoad.honnoki.R
import kotlinx.coroutines.delay
import timber.log.Timber

class DownloadJob(context: Context, workerParams: WorkerParameters) : CoroutineWorker(context, workerParams) {

    private val notificationManager by lazy {
        applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    override suspend fun doWork(): Result {
        startForeground()
        delay(50000)
        return Result.success()
    }

    private suspend fun startForeground() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel()
            }
            val intent = WorkManager.getInstance(applicationContext).createCancelPendingIntent(id)
            val notification = NotificationCompat.Builder(applicationContext, id.toString())
                .setContentTitle("title")
                .setTicker("ticker")
                .setContentText("progress")
                .setSmallIcon(R.drawable.android_adb)
                .setOngoing(true)
                .addAction(android.R.drawable.ic_delete, "cancel", intent)
                .build()
            val foregroundInfo = ForegroundInfo(1, notification)
            setForeground(foregroundInfo)
        } catch (e: IllegalStateException) {
            Timber.i(e, "Failed to start foreground")
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val name = getString(R.string.channel_name)
//            val descriptionText = getString(R.string.channel_description)
            val name = "name"
            val descriptionText = "desc"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(id.toString(), name, importance).apply {
                description = descriptionText
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

}
