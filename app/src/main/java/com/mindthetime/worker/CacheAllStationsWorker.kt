package com.mindthetime.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.mindthetime.repository.TflRepository

class CacheAllStationsWorker(appContext: Context, workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val repository = TflRepository(applicationContext)
        repository.fetchAllAndCache()
        return Result.success()
    }
}