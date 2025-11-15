package com.mindthetime

import android.app.Application
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.mindthetime.worker.CacheAllStationsWorker

class MindTheTimeApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        enqueueCacheWorker()
    }

    private fun enqueueCacheWorker() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<CacheAllStationsWorker>()
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueueUniqueWork(
            "cache-all-stations",
            ExistingWorkPolicy.KEEP,
            workRequest
        )
    }
}