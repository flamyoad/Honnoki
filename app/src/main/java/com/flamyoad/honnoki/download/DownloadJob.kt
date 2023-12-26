package com.flamyoad.honnoki.download

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.delay
import timber.log.Timber

class DownloadJob(context: Context, workerParams: WorkerParameters)
    : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return Result.success()
    }

    private suspend fun startForeground() {
        try {
            setForeground(getForegroundInfo())
            delay(500)
        } catch (e: IllegalStateException) {
            Timber.i(e, "Failed to start foreground")
        }
    }

}
