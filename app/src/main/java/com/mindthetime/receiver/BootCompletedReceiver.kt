package com.mindthetime.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.mindthetime.worker.CacheAllStationsWorker

class BootCompletedReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val workRequest = OneTimeWorkRequestBuilder<CacheAllStationsWorker>().build()
            WorkManager.getInstance(context).enqueue(workRequest)
        }
    }
}